package at.cath.simpletabs.gui

import at.cath.simpletabs.gui.settings.CreationDesc
import at.cath.simpletabs.gui.settings.SelectionDesc
import at.cath.simpletabs.tabs.TabMenu
import io.github.cottonmc.cotton.gui.client.CottonClientScreen
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.text.LiteralText
import kotlin.properties.Delegates

class TabScreen(originalChatText: String?) : ChatScreen(originalChatText) {
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
        this.tabHeight = font.fontHeight + 2 * componentPadding
        // 12 = text bar height
        this.tabY = height - 12 - tabHeight - cornerOffset * 2 - 2

        (MinecraftClient.getInstance().inGameHud.chatHud as? TabMenu)?.let {
            tabMenu = it
        } ?: throw IllegalStateException("ChatHUD is not custom; cannot initialize tabs screen")

        drawUI()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (MouseAction.create(button) == MouseAction.RIGHT_CLICK) {
            tabMenu.handleClickTranslation(mouseX, mouseY)
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    private fun drawUI() {
        clearUI()

        val pageLeftBtn = addButton(
            TabButton(
                cornerOffset,
                tabY,
                font.getStringWidth("<") + componentPadding,
                tabHeight,
                LiteralText("<"),
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
            addButton(
                TabButton(
                    tabsStartX + i * tabWidth + if (i > 0) elementOffset * i else 0,
                    tabY,
                    tabWidth,
                    tabHeight,
                    LiteralText(font.trimToWidth(tab.name, tabButtonWidth)),
                    tab,
                    clickCallback = object : MouseActionCallback {
                        override fun onLeftClick() {
                            tabMenu.navigateTo(tab.uuid)
                        }

                        override fun onRightClick() {
                            MinecraftClient.getInstance().openScreen(CottonClientScreen(SelectionDesc(tab)))
                        }

                        override fun onMouseMiddleClick() {
                            if (tabMenu.removeTab(tab.uuid)) drawUI()
                        }
                    })
            )
        }

        val controlStartX = tabsStartX + tabMenu.showPerPage * tabWidth + (tabMenu.showPerPage - 1) * 2 + elementOffset

        val pageRightBtn = addButton(
            TabButton(
                controlStartX,
                tabY,
                font.getStringWidth(">") + componentPadding,
                tabHeight,
                LiteralText(">"),
                clickCallback =
                object : MouseActionCallback {
                    override fun onLeftClick() {
                        if (tabMenu.nextPage()) drawUI()
                    }
                }
            )
        )

        val addTabBtn = addButton(
            TabButton(
                pageRightBtn.x + pageRightBtn.width + 15,
                tabY,
                font.getStringWidth("+") + 2 * componentPadding,
                tabHeight,
                LiteralText("+"),
                clickCallback =
                object : MouseActionCallback {
                    override fun onLeftClick() {
                        MinecraftClient.getInstance().openScreen(CottonClientScreen(CreationDesc(256, 100, tabMenu)))
                    }
                }
            )
        )

        addButton(
            TabButton(
                addTabBtn.x + addTabBtn.width + elementOffset,
                tabY,
                font.getStringWidth("${tabMenu.activeGroup}") + 2 * componentPadding,
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
        children().removeAll { it is TabComponent }
    }
}
