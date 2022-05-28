package at.cath.simpletabs.gui

import at.cath.simpletabs.TabsMod
import at.cath.simpletabs.tabs.ChatMenu
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import kotlin.math.min

class ChatTabScreen(originalChatText: String?) : ChatScreen(originalChatText) {

    private val componentPadding = 5
    private val cornerOffset = 2
    private val showPerPage = 3
    private val currentPage = 0
    private val tabButtonWidth = 40

    private lateinit var tabMenu: ChatMenu
    private val SETTINGS_ICON = Identifier(TabsMod.MOD_ID, "textures/ui/settings.png")

    override fun init() {
        super.init()

        (client?.inGameHud?.chatHud as? ChatMenu)?.let {
            tabMenu = it
        } ?: throw IllegalStateException("ChatHUD is not custom; cannot initialize tabs screen")

        drawTabs()
    }

    fun drawSettings() {
        /*
        addDrawableChild(
            TexturedButtonWidget(
                cornerOffset + 20 * tabMenu.tabList.size,
                height - chatField.height - tabHeight - cornerOffset * 2,
                32,
                32,
                0,
                0,
                0,
                SETTINGS_ICON,
                32,
                32
            )
            {

            }
        )
         */
    }

    private fun drawTabs() {
        val startAtTab = currentPage * showPerPage
        val tabHeight = textRenderer.fontHeight + 2 * componentPadding
        val tabWidth = tabButtonWidth + 2 * componentPadding
        val tabs = tabMenu.tabList

        for (i in startAtTab until min(startAtTab + showPerPage, tabs.size)) {
            with(tabs[i]) {
                addDrawableChild(
                    TabButton(
                        cornerOffset + i * tabWidth + 10,
                        height - chatField.height - tabHeight - cornerOffset * 2,
                        tabWidth,
                        tabHeight,
                        Text.of(textRenderer.trimToWidth(name, tabButtonWidth)),
                        object : MouseActionCallback {
                            override fun onLeftClick() {
                                println("left!")
                            }

                            override fun onRightClick() {
                                println("right!")
                            }

                            override fun onMouseMiddleClick() {
                                println("middle!")
                            }
                        })
                )
            }
        }
    }
}
