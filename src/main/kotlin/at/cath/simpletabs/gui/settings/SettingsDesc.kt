package at.cath.simpletabs.gui.settings

import io.github.cottonmc.cotton.gui.client.CottonClientScreen
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import net.minecraft.client.MinecraftClient
import net.minecraft.text.LiteralText
import net.minecraft.text.Style
import net.minecraft.util.Formatting

class SettingsDesc<T>(panel: T, parent: LightweightGuiDescription, validator: (SaveCallback) -> Unit) :
    LightweightGuiDescription() where T : WGridPanel, T : SaveCallback {
    init {
        this.setRootPanel(panel)
        val confirmButton = WButton(LiteralText("Confirm").setStyle(Style().setColor(Formatting.GREEN)))
        confirmButton.setOnClick {
            validator(panel)
        }

        val homeButton = WButton(LiteralText("Return").setStyle(Style().setColor(Formatting.YELLOW)))
        homeButton.setOnClick {
            MinecraftClient.getInstance().openScreen(CottonClientScreen(parent))
        }

        with(rootPanel as WGridPanel) {
            add(homeButton, 6, 9, 5, 1)
            add(confirmButton, 0, 9, 5, 1)
        }
        rootPanel.validate(this)
    }
}