package at.cath.simpletabs.tabs

import at.cath.simpletabs.TabsMod
import at.cath.simpletabs.gui.ChatTabScreen
import at.cath.simpletabs.mixins.MixinHudUtility
import at.cath.simpletabs.translate.RetrofitDeepl
import at.cath.simpletabs.utility.SimpleColour
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.ChatHud
import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.client.option.ChatVisibility
import net.minecraft.client.util.ChatMessages
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.math.MathHelper
import java.util.*
import kotlin.math.min


class TabMenu(var client: MinecraftClient, serialized: String? = null) : ChatHud(client) {

    var activeGroup: Int = 0
    private var activePage: Int = 0
    private var selectedTab: String = ""

    val showPerPage = 4
    var pageTabs = mutableListOf(linkedMapOf<String, ChatTab>())

    private val visibleMessages = (this as MixinHudUtility).visibleMessages
    private val localMessageHistory = (this as MixinHudUtility).localMessageHistory

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
                var repeatCount = 0
                if (it.acceptsMessage(message.string)) {
                    val incoming = message.shallowCopy()

                    if (it.messages.isNotEmpty()) {
                        val (extractedMsg, repeats) = extractRepeatMsg(it.messages.last())
                        if (message.string == extractedMsg.string) {
                            repeatCount = repeats
                            repeatCount += 1
                        }
                    }

                    if (repeatCount > 0)
                        incoming.appendRepeatMsg(repeatCount + 1)

                    if (it.uuid == selectedTab) {
                        if (repeatCount > 0) {
                            visibleMessages.removeFirst()
                            localMessageHistory.removeFirst()
                        }
                        super.addMessage(incoming)

                    } else if (!it.muted && pageTabs[activeGroup].containsKey(it.uuid)) {
                        it.unreadCount++
                    }

                    if (repeatCount > 0) it.messages.removeLast()
                    it.messages += incoming

                }
            }
        }
    }

    fun handleClickTranslation(x: Double, y: Double) {
        val relativeMsg = getMessageAt(x, y)

        if (relativeMsg != null) {
            val (indexWhole, indexVisible) = relativeMsg
            val tab = getSelectedTab()!!

            // don't translate when tab is not set to
            if (tab.language.targetLanguage.isEmpty()) {
                return
            }

            val incoming = localMessageHistory[indexWhole].text

            // don't translate if already translated
            if (incoming is TranslatableText && incoming.key == "chat.simpletabs.translated") {
                return
            }
            val chatWidth = MathHelper.floor(this.width.toDouble() / this.chatScale)
            CoroutineScope(Dispatchers.IO).launch {
                val response = RetrofitDeepl.api.getTranslation(incoming.string, tab.language.targetLanguage)
                if (response.isSuccessful) {
                    val translation = Text.of(response.body()!!.translations[0].translatedText)
                    val formattedTranslation =
                        TranslatableText(
                            "chat.simpletabs.translated",
                            tab.language.targetLanguage.uppercase(),
                            translation
                        )
                            .setStyle(Style.EMPTY.withColor(SimpleColour(128, 27, 87, 255).packedRgb))

                    val lineBreakTranslation = ChatMessages.breakRenderedChatMessageLines(
                        formattedTranslation,
                        chatWidth,
                        this@TabMenu.client.textRenderer
                    )

                    val lineBreakIncoming = ChatMessages.breakRenderedChatMessageLines(
                        incoming,
                        chatWidth,
                        this@TabMenu.client.textRenderer
                    )

                    for ((idx, msgIdx) in (indexVisible downTo (indexVisible - lineBreakTranslation.size + 1).coerceAtMost(
                        indexVisible - lineBreakIncoming.size + 1
                    )).withIndex()) {
                        if (msgIdx < 0) {
                            visibleMessages.add(
                                0,
                                ChatHudLine(client.inGameHud.ticks, lineBreakTranslation[idx], 0)
                            )

                        } else if (idx >= lineBreakTranslation.size) {
                            visibleMessages.removeAt(msgIdx)
                        } else if (idx < lineBreakIncoming.size) {
                            visibleMessages[msgIdx] = ChatHudLine(client.inGameHud.ticks, lineBreakTranslation[idx], 0)
                        } else {
                            visibleMessages.add(
                                msgIdx + 1,
                                ChatHudLine(client.inGameHud.ticks, lineBreakTranslation[idx], 0)
                            )
                        }
                    }

                    localMessageHistory[indexWhole] = ChatHudLine(client.inGameHud.ticks, formattedTranslation, 0)
                } else {
                    TabsMod.logger.error("Encountered error code ${response.code()} when fetching translation: ${response.message()}")
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
                    clear(false)
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
        } else if (pageTabs[activeGroup].size > 0) {
            pageTabs.add(++activeGroup, linkedMapOf())

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
            tab.messages.forEach {
                (this as MixinHudUtility).addMessageWithoutLog(
                    it,
                    0,
                    client.inGameHud.ticks,
                    false
                )
            }

            tab.unreadCount = 0
            selectedTab = tab.uuid
        }
    }

    fun getSelectedTab(): ChatTab? = pageTabs[activeGroup][selectedTab]

    // abomination
    private fun getMessageAt(x: Double, y: Double): Pair<Int, Int>? {
        return if (client.currentScreen is ChatTabScreen && !this.client.options.hudHidden && client.options.chatVisibility != ChatVisibility.HIDDEN) {
            var d = x - 2.0
            var e = this.client.window.scaledHeight.toDouble() - y - 40.0
            d = MathHelper.floor(d / this.chatScale).toDouble()
            e = MathHelper.floor(e / (this.chatScale * (this.client.options.chatLineSpacing + 1.0))).toDouble()
            if (d >= 0.0 && e >= 0.0) {
                val i = this.visibleLineCount.coerceAtMost(visibleMessages.size)
                if (d <= MathHelper.floor(this.width.toDouble() / this.chatScale).toDouble()) {
                    Objects.requireNonNull(this.client.textRenderer)
                    if (e < (9 * i + i).toDouble()) {
                        Objects.requireNonNull(this.client.textRenderer)
                        var countVisibleMsg = (e / 9.0 + (this as MixinHudUtility).scrolledLines.toDouble()).toInt()
                        if (countVisibleMsg >= 0 && countVisibleMsg < visibleMessages.size) {
                            val chatWidth = MathHelper.floor(this.width.toDouble() / this.chatScale)

                            var countSplits = 0
                            var countWholeMsg = 0

                            run breaking@{
                                localMessageHistory.forEachIndexed { i, msg ->
                                    val increase = ChatMessages.breakRenderedChatMessageLines(
                                        msg.text,
                                        chatWidth,
                                        this.client.textRenderer
                                    ).size

                                    if (countSplits + (increase - 1) >= countVisibleMsg) {
                                        countWholeMsg = i
                                        if (countSplits + (increase - 1) > countVisibleMsg) {
                                            countVisibleMsg += (increase - 1 + (countSplits - countVisibleMsg))
                                        }
                                        return@breaking
                                    }

                                    countSplits += increase
                                }
                            }
                            return Pair(countWholeMsg, countVisibleMsg)
                        }
                    }
                }
                null
            } else {
                null
            }
        } else {
            null
        }
    }

    private fun extractRepeatMsg(msg: Text): Pair<Text, Int> {
        with(msg.siblings) {
            if (size > 0) {
                val lastComponent = find { it is TranslatableText && it.key == "chat.simpletabs.repeat" }
                if (lastComponent != null) {
                    val repeatCount = lastComponent.string.filter(Char::isDigit).toInt() - 1
                    val extractedMsg = msg.shallowCopy()
                    extractedMsg.siblings.removeIf { it == lastComponent }
                    return Pair(extractedMsg, repeatCount)
                }
            }
        }
        return Pair(msg, 0)
    }

    private fun Text.appendRepeatMsg(repeatCount: Int): Text {
        this.siblings.add(
            TranslatableText(
                "chat.simpletabs.repeat",
                repeatCount
            ).setStyle(Style.EMPTY.withColor(Formatting.GRAY))
        )
        return this
    }
}