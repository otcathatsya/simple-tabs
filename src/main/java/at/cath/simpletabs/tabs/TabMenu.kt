package at.cath.simpletabs.tabs

import at.cath.simpletabs.TabsMod
import at.cath.simpletabs.gui.ChatTabScreen
import at.cath.simpletabs.mixins.MixinHudUtility
import kotlinx.serialization.json.Json
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.ChatHud
import net.minecraft.client.gui.hud.MessageIndicator
import net.minecraft.network.message.ChatVisibility
import net.minecraft.network.message.MessageSignatureData
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TranslatableTextContent
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

    override fun addMessage(message: Text, signature: MessageSignatureData?, indicator: MessageIndicator?) {
        pageTabs.forEach { tabMap ->
            tabMap.values.forEach {
                var repeatCount = 0
                if (it.acceptsMessage(message.string)) {
                    val incoming = message.copy()
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
                        super.addMessage(incoming, signature, indicator)
                    } else if (!it.muted && pageTabs[activeGroup].containsKey(it.uuid)) {
                        it.unreadCount++
                    }

                    if (repeatCount > 0) it.messages.removeLast()
                    it.messages += incoming

                }
            }
        }
    }

    /*
     fun handleClickTranslation(x: Double, y: Double) {
         val msgIndices = getLocalChatIndicesAt(x, y)

         if (msgIndices != null) {
             val (indexText, indexVisible) = msgIndices
             val tab = getSelectedTab()!!

             // don't translate when tab is not set to
             if (tab.language.targetLanguage.isEmpty()) {
                 return
             }

             val incoming = localMessageHistory[indexText].content

             // don't translate if already translated
             if (incoming is TranslatableTextContent && incoming.key == "chat.simpletabs.translated") {
                 return
             }

             CoroutineScope(Dispatchers.IO).launch {
                 val response = RetrofitDeepl.api.getTranslation(incoming.string, tab.language.targetLanguage)
                 if (response.isSuccessful) {
                     val translation = Text.of(response.body()!!.translations[0].translatedText)
                     val formattedTranslation =
                         TranslatableTextContent(
                             "chat.simpletabs.translated",
                             tab.language.targetLanguage.uppercase(),
                             translation
                         )
                             .setStyle(Style.EMPTY.withColor(SimpleColour(128, 27, 87, 255).packedRgb))

                     val chatWidth = MathHelper.floor(this@TabMenu.width.toDouble() / this@TabMenu.chatScale)

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

                     localMessageHistory[indexText] = ChatHudLine(client.inGameHud.ticks, formattedTranslation, 0)
                 } else {
                     TabsMod.logger.error("Encountered error code ${response.code()} when fetching translation: ${response.message()}")
                 }
             }
         }
     }
     */

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
                (this as MixinHudUtility).addMessageWithoutLogs(
                    it,
                    null,
                    this.client.inGameHud.ticks,
                    if (this.client.isConnectedToLocalServer) MessageIndicator.singlePlayer() else MessageIndicator.system(),
                    false
                )
            }

            tab.unreadCount = 0
            selectedTab = tab.uuid
        }
    }

    fun getSelectedTab(): ChatTab? = pageTabs[activeGroup][selectedTab]

    private fun extractRepeatMsg(msg: Text): Pair<Text, Int> {
        with(msg.siblings) {
            if (size > 0) {
                val lastComponent =
                    find { it.content is TranslatableTextContent && (it.content as TranslatableTextContent).key == "chat.simpletabs.repeat" }
                if (lastComponent != null) {
                    println("last component is not null, getting number")
                    val repeatCount = lastComponent.string.filter(Char::isDigit).toInt() - 1
                    val extractedMsg = msg.copy()
                    extractedMsg.siblings.removeIf { it == lastComponent }
                    return Pair(extractedMsg, repeatCount)
                }
            }
        }
        return Pair(msg, 0)
    }

    private fun Text.appendRepeatMsg(repeatCount: Int): Text {
        this.siblings.add(
            MutableText.of(
                TranslatableTextContent(
                    "chat.simpletabs.repeat",
                    "www",
                    arrayOf(repeatCount)
                )
            ).setStyle(Style.EMPTY.withColor(Formatting.GRAY))
        )
        return this
    }

    private fun getLocalChatIndicesAt(x: Double, y: Double): Pair<Int, Int>? {
        return if (client.currentScreen is ChatTabScreen && !this.client.options.hudHidden && client.options.chatVisibility.value != ChatVisibility.HIDDEN) {
            var d = x - 2.0
            var e = this.client.window.scaledHeight.toDouble() - y - 40.0
            d = MathHelper.floor(d / this.chatScale).toDouble()
            e = MathHelper.floor(e / (this.chatScale * (this.client.options.chatLineSpacing.value + 1.0))).toDouble()
            if (d >= 0.0 && e >= 0.0) {
                val i = this.visibleLineCount.coerceAtMost(visibleMessages.size)
                if (d <= MathHelper.floor(this.width.toDouble() / this.chatScale).toDouble()) {
                    Objects.requireNonNull(this.client.textRenderer)
                    if (e < (9 * i + i).toDouble()) {
                        Objects.requireNonNull(this.client.textRenderer)
                        var indexVisibleMsg = (e / 9.0 + (this as MixinHudUtility).scrolledLines.toDouble()).toInt()
                        if (indexVisibleMsg >= 0 && indexVisibleMsg < visibleMessages.size) {
                            var sumVisibleMsgs = 0
                            var countWholeMsgs = 0

                            for ((idx, msg) in localMessageHistory.withIndex()) {
                                val increase = countRenderedMessageSplits(msg.content)

                                val lookAhead = sumVisibleMsgs + (increase - 1)
                                if (lookAhead >= indexVisibleMsg) {
                                    countWholeMsgs = idx
                                    // multi-line message; correct the index to point to the start of the message
                                    if (lookAhead > indexVisibleMsg) {
                                        indexVisibleMsg += (increase - 1 + (sumVisibleMsgs - indexVisibleMsg))
                                    }
                                    break
                                }
                                sumVisibleMsgs += increase
                            }
                            return Pair(countWholeMsgs, indexVisibleMsg)
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

    private fun countRenderedMessageSplits(text: Text): Int {
        var count = 0
        client.textRenderer.textHandler
            .wrapLines(text, width, Style.EMPTY) { _, _ -> count++ }
        return count
    }
}