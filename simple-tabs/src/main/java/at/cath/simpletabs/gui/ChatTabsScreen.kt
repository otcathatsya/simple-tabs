package at.cath.simpletabs.gui

import at.cath.simpletabs.tabs.CustomChatMenu
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text

class ChatTabsScreen(originalChatText: String?) : ChatScreen(originalChatText) {

    private val componentPadding = 5

    override fun init() {
        super.init()
        println("so width's $width and height ig $height")

        val tabMenu = client?.inGameHud?.chatHud as? CustomChatMenu

        if (tabMenu != null) {
            tabMenu.tabList.forEach {
                addDrawableChild(ButtonWidget(
                    width / 2,
                    height / 2,
                    textRenderer.getWidth(it.name),
                    textRenderer.fontHeight + 2 * componentPadding,
                    Text.of(it.name)
                ) {
                })
            }

        } else {
            throw IllegalStateException("ChatHUD is not custom; cannot initialize tabs screen")
        }
    }
}
