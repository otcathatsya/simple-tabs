package at.cath.simpletabs.tabs

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.ChatHud
import net.minecraft.text.Text

class CustomChatMenu(client: MinecraftClient?) : ChatHud(client) {

    private var currentIndex: Int = 0
    var tabList = mutableListOf(CustomChatTab())

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
        if (chatIndex < tabList.size) {
            val tab = tabList[chatIndex]
            tabList[currentIndex]

            clear(true)
            tab.messages.forEach { addMessage(Text.of(it)) }

            tab.unreadCount = 0
            currentIndex = chatIndex
        } else {
            throw IllegalArgumentException("Cannot navigate to chat tab; index out of range")
        }
    }
}