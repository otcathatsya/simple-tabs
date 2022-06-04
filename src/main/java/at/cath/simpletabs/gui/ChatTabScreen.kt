package at.cath.simpletabs.gui

import at.cath.simpletabs.gui.settings.SettingsDescription
import at.cath.simpletabs.gui.settings.TabUIScreen
import at.cath.simpletabs.gui.settings.TabUpdatePanel
import at.cath.simpletabs.tabs.TabMenu
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import kotlin.properties.Delegates

class ChatTabScreen(originalChatText: String?) : ChatScreen(originalChatText) {
    private val componentPadding = 5
    private val cornerOffset = 2
    private val elementOffset = 2

    private val tabButtonWidth = 50
    private val tabWidth = tabButtonWidth + 2 * componentPadding
    private var tabHeight by Delegates.notNull<Int>()

    private var tabY by Delegates.notNull<Int>()

    private lateinit var tabMenu: TabMenu

    override fun init() {
        super.init()
        this.tabHeight = textRenderer.fontHeight + 2 * componentPadding
        this.tabY = height - chatField.height - tabHeight - cornerOffset * 2 - 2

        (client?.inGameHud?.chatHud as? TabMenu)?.let {
            tabMenu = it
        } ?: throw IllegalStateException("ChatHUD is not custom; cannot initialize tabs screen")

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
        val tabsStartX = pageLeftBtn.x + pageLeftBtn.width + elementOffset

        tabs.forEachIndexed { i, tab ->
            addDrawableChild(
                TabButton(
                    tabsStartX + i * tabWidth + if (i > 0) elementOffset * i else 0,
                    tabY,
                    tabWidth,
                    tabHeight,
                    Text.of(textRenderer.trimToWidth(tab.name, tabButtonWidth)),
                    tab,
                    clickCallback = object : MouseActionCallback {
                        override fun onLeftClick() {
                            tabMenu.navigateTo(tab.uuid)
                        }

                        override fun onRightClick() {
                            client?.setScreen(TabUIScreen(SettingsDescription(tab)))
                        }

                        override fun onMouseMiddleClick() {
                            if (tabMenu.removeTab(tab.uuid)) drawUI()
                        }
                    })
            )
        }

        val controlStartX = tabsStartX + tabMenu.showPerPage * tabWidth + (tabMenu.showPerPage - 1) * 2 + elementOffset

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

        val addTabBtn = addDrawableChild(
            TabButton(
                pageRightBtn.x + pageRightBtn.width + 15,
                tabY,
                textRenderer.getWidth("+") + 2 * componentPadding,
                tabHeight,
                Text.of("+"),
                clickCallback =
                object : MouseActionCallback {
                    override fun onLeftClick() {
                        client?.setScreen(TabUIScreen(object : LightweightGuiDescription() {
                            init {
                                val root = TabUpdatePanel(256, 100, null, tabMenu)
                                setRootPanel(root)
                                root.validate(this)
                            }
                        }))
                    }
                }
            )
        )

        addDrawableChild(
            TabButton(
                addTabBtn.x + addTabBtn.width + elementOffset,
                tabY,
                textRenderer.getWidth("${tabMenu.activeGroup}") + 2 * componentPadding,
                tabHeight,
                LiteralText("${tabMenu.activeGroup}"),
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