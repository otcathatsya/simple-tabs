package at.cath.simpletabs.gui

import at.cath.simpletabs.tabs.ChatMenu
import at.cath.simpletabs.tabs.ChatTab
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.text.Text
import kotlin.properties.Delegates

class ChatTabScreen(originalChatText: String?) : ChatScreen(originalChatText) {
    private val componentPadding = 5
    private val cornerOffset = 2
    private val controlOffset = 10

    private val tabButtonWidth = 40
    private val tabWidth = tabButtonWidth + 2 * componentPadding
    private var tabHeight by Delegates.notNull<Int>()

    private var tabY by Delegates.notNull<Int>()

    private lateinit var tabMenu: ChatMenu

    override fun init() {
        super.init()
        this.tabHeight = textRenderer.fontHeight + 2 * componentPadding
        this.tabY = height - chatField.height - tabHeight - cornerOffset * 2

        (client?.inGameHud?.chatHud as? ChatMenu)?.let {
            tabMenu = it
        } ?: throw IllegalStateException("ChatHUD is not custom; cannot initialize tabs screen")

        tabMenu.addTab(ChatTab("Hola"))
        drawUI()
    }

    private fun drawUI() {
        clearUI()

        val pageLeftBtn = addDrawableChild(
            TabButton(
                cornerOffset,
                tabY,
                textRenderer.getWidth("<") + componentPadding,
                tabHeight,
                Text.of("<"),
                clickCallback =
                object : MouseActionCallback {
                    override fun onLeftClick() {
                        if (tabMenu.previousPage()) drawUI()
                    }
                }
            )
        )

        val tabs = tabMenu.getActiveTabs()

        val tabsStartX = pageLeftBtn.x + pageLeftBtn.width + controlOffset

        tabs.forEachIndexed { i, tab ->
            addDrawableChild(
                TabButton(
                    tabsStartX + i * tabWidth + if (i > 0) 2 * i else 0,
                    tabY,
                    tabWidth,
                    tabHeight,
                    Text.of(textRenderer.trimToWidth(tab.name, tabButtonWidth)),
                    clickCallback = object : MouseActionCallback {
                        override fun onLeftClick() {
                            tabMenu.navigateTo(tab.uuid)
                        }

                        override fun onRightClick() {
                            // TODO: Implement settings
                        }

                        override fun onMouseMiddleClick() {
                            tabMenu.removeTab(tab.uuid)
                        }
                    })
            )
        }

        val controlStartX = tabsStartX + tabMenu.showPerPage * tabWidth + (tabMenu.showPerPage - 1) * 2 + controlOffset

        // TODO: builder for those buttons
        val pageRightBtn = addDrawableChild(
            TabButton(
                controlStartX,
                tabY,
                textRenderer.getWidth(">") + componentPadding,
                tabHeight,
                Text.of(">"),
                clickCallback =
                object : MouseActionCallback {
                    override fun onLeftClick() {
                        if (tabMenu.nextPage()) drawUI()
                    }
                }
            )
        )

        addDrawableChild(
            TabButton(
                pageRightBtn.x + pageRightBtn.width + 1,
                tabY,
                textRenderer.getWidth("${tabMenu.activePage}/${tabMenu.activeGroup}") + 2 * componentPadding,
                tabHeight,
                Text.of("${tabMenu.activePage}/${tabMenu.activeGroup}"),
                clickCallback =
                object : MouseActionCallback {
                    override fun onLeftClick() {
                        if (tabMenu.cycleForward()) drawUI()
                    }

                    override fun onRightClick() {
                        if (tabMenu.cycleBackward()) drawUI()
                    }
                }
            )
        )
    }

    private fun clearUI() {
        children().filterIsInstance<TabGUIComponent>().forEach(::remove)
    }
}
