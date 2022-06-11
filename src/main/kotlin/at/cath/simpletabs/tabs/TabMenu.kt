package at.cath.simpletabs.tabs

import at.cath.simpletabs.TabsMod
import at.cath.simpletabs.mixins.ChatHudInvoker
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.ChatHud
import net.minecraft.text.Text
import kotlin.math.min

class TabMenu(var client: MinecraftClient, serialized: String? = null) : ChatHud(client) {

    var activeGroup: Int = 0
    private var activePage: Int = 0
    private var selectedTab: String = ""

    val showPerPage = 4
    var pageTabs = mutableListOf(linkedMapOf<String, ChatTab>())

    init {
        if (serialized != null) {
            try {
                this.pageTabs = Json.decodeFromString(serialized)
            } catch (ex: Exception) {
                TabsMod.logger.error("Encountered invalid tabs config format")
            }
        }
        if (pageTabs.all { it.isEmpty() }) {
            val defaultTab = ChatTab("General")
            pageTabs[0] = linkedMapOf(defaultTab.uuid to defaultTab)
            navigateTo(defaultTab.uuid)
        } else if (selectedTab.isEmpty()) {
            navigateTo(pageTabs.first { it.isNotEmpty() }.keys.first())
        }
    }

    override fun addMessage(message: Text) {
        pageTabs.forEach { tabMap ->
            tabMap.values.forEach {
                if (it.acceptsMessage(message.string)) {
                    if (it.uuid == selectedTab)
                        super.addMessage(message)
                    else if (!it.muted)
                        it.unreadCount++

                    it.messages += message
                }
            }
        }
    }

    fun addTab(chatTab: ChatTab) {
        pageTabs[activeGroup][chatTab.uuid] = chatTab
    }

    fun removeTab(tabId: String): Boolean {
        with(pageTabs[activeGroup]) {
            if (containsKey(tabId)) {
                remove(tabId)

                if (selectedTab == tabId) {
                    selectedTab = ""
                    clear()
                }
                return true
            }
            return false
        }
    }

    fun nextPage(): Boolean {
        if (pageTabs[activeGroup].size > showPerPage * (activePage + 1)) {
            activePage++
            return true
        }
        return false
    }

    fun previousPage(): Boolean {
        if (activePage - 1 >= 0) {
            activePage--
            return true
        }
        return false
    }

    fun getActiveTabs(): List<ChatTab> {
        val tabsInGroup = pageTabs[activeGroup].values.toList()
        val from = activePage * showPerPage
        val to = from + showPerPage

        return tabsInGroup.slice(from until min(tabsInGroup.size, to))
    }

    fun cycleForward(): Boolean {
        if (activeGroup + 1 < pageTabs.size) {
            activeGroup++
            selectedTab = ""
            clear(false)
            return true
        } else if (pageTabs[activeGroup].size > 1) {
            val defaultTab = ChatTab("General")
            pageTabs.add(++activeGroup, linkedMapOf(defaultTab.uuid to defaultTab))

            navigateTo(defaultTab.uuid)
            clear(false)
            return true
        }
        return false
    }

    fun cycleBackward(): Boolean {
        if (activeGroup - 1 >= 0) {
            activeGroup--
            selectedTab = ""
            clear(false)
            return true
        }
        return false
    }

    fun navigateTo(tabId: String) {
        if (pageTabs[activeGroup].containsKey(tabId) && selectedTab != tabId) {
            val tab = pageTabs[activeGroup][tabId]!!
            clear(false)

            // mixin invoker to avoid printing a new message to logs when switching tabs
            tab.messages.forEach { (this as ChatHudInvoker).addMessageWithoutLog(it, 0, client.inGameHud.ticks, false) }

            tab.unreadCount = 0
            selectedTab = tab.uuid
        }
    }
}