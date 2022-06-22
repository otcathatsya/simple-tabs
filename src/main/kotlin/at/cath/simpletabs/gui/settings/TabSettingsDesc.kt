package at.cath.simpletabs.gui.settings

import at.cath.simpletabs.tabs.ChatTab
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WTabPanel
import io.github.cottonmc.cotton.gui.widget.data.Insets
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.ActionResult

class TabSettingsDesc(tab: ChatTab) : LightweightGuiDescription() {

    init {
        val root = WGridPanel()
        setRootPanel(root)
        root.setSize(256, 240)
        root.insets = Insets.ROOT_PANEL

        val panels = WTabPanel()

        val tabWidth = (root.width * 0.9).toInt()
        val tabHeight = (root.height * 0.8).toInt()

        val updatePanel = TabUpdatePanel(tabWidth, tabHeight, tab)
        val designPanel = TabDesignPanel(tabWidth, tabHeight, tab)
        val tabPanels: List<SaveSettingsCallback> = listOf(updatePanel, designPanel)

        panels.add(updatePanel) {
            it.icon(ItemIcon(ItemStack(Items.QUARTZ)))
            it.title(Text.of("Settings"))
        }

        panels.add(designPanel) {
            it.icon(ItemIcon(ItemStack(Items.PEONY)))
            it.title(Text.of("Design"))
        }

        val confirmButton = WButton(Text.of("Confirm"))
        confirmButton.setOnClick {
            tabPanels.forEach {
                if (it.onClose() == ActionResult.FAIL) {
                    confirmButton.icon = ItemIcon(ItemStack(Items.BARRIER))
                    return@setOnClick
                }
            }
            MinecraftClient.getInstance().setScreen(null)
        }

        root.add(panels, 0, 0)
        root.add(confirmButton, 1, 11, 11, 1)

        root.validate(this)
    }
}