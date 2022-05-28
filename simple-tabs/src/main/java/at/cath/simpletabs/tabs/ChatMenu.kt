package at.cath.simpletabs.tabs

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.ChatHud
import net.minecraft.text.Text

class ChatMenu(client: MinecraftClient?) : ChatHud(client) {

    private var currentIndex: Int = 0
    var tabList =
        mutableListOf(ChatTab("Donaudampfschifffahrtselektrizitätenhauptbetriebswerkbauunterbeamtengesellschaft"))

    override fun addMessage(message: Text?) {
        val msg = message.toString()
        tabList.forEach {
            if (it.acceptsMessage(msg)) {
                if (it == tabList[currentIndex])
                    super.addMessage(message)
                else if (!it.muted)
                    it.unreadCount++

                it.messages += msg
            }
        }
    }

    fun removeTab(chatIndex: Int): Boolean {
        if (tabList.size == 1)
            return false
        navigateTo(--currentIndex)
        tabList.removeAt(chatIndex)
        return true
    }

    fun navigateTo(chatIndex: Int) {
        if (chatIndex < tabList.size && chatIndex != currentIndex) {
            val tab = tabList[chatIndex]
            tabList[currentIndex]

            clear(true)
            tab.messages.forEach { addMessage(Text.of(it)) }

            tab.unreadCount = 0
            currentIndex = chatIndex
        }
    }
}