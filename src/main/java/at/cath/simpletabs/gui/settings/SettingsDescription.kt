package at.cath.simpletabs.gui.settings

import at.cath.simpletabs.tabs.ChatTab
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WTabPanel
import io.github.cottonmc.cotton.gui.widget.data.Insets
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text

class SettingsDescription(tab: ChatTab) : LightweightGuiDescription() {
    init {
        val root = WGridPanel()
        setRootPanel(root)
        root.setSize(256, 240)
        root.insets = Insets.ROOT_PANEL

        val panels = WTabPanel()
        val tabWidth = (root.width * 0.9).toInt()
        val tabHeight = (root.height * 0.8).toInt()

        panels.add(TabUpdatePanel(tabWidth, tabHeight, tab)) {
            it.icon(ItemIcon(ItemStack(Items.QUARTZ)))
            it.title(Text.of("Settings"))
        }

        panels.add(TabDesignPanel(tabWidth, tabHeight, tab)) {
            it.icon(ItemIcon(ItemStack(Items.PEONY)))
            it.title(Text.of("Design"))
        }
        root.add(panels, 0, 0)

        root.validate(this)
    }
}