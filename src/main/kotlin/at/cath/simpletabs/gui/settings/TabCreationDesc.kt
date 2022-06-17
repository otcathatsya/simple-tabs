package at.cath.simpletabs.gui.settings

import at.cath.simpletabs.tabs.ChatTab
import at.cath.simpletabs.tabs.TabMenu
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.WButton
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.util.ActionResult

class TabCreationDesc(width: Int, height: Int, tabMenu: TabMenu) : LightweightGuiDescription() {
    init {
        val root = TabUpdatePanel(width, height, null)
        setRootPanel(root)

        val confirmButton = WButton(Text.of("Confirm"))
        confirmButton.setOnClick {
            with(root) {
                if (root.onClose() == ActionResult.PASS) {
                    tabMenu.addTab(
                        ChatTab(
                            name = inputName.text, regex = inputRegex.text,
                            inverted = toggleInverted.toggle, muted = toggleMuted.toggle,
                            literal = toggleLiteral.toggle
                        )
                    )
                    MinecraftClient.getInstance().setScreen(null)
                }
            }
        }
        root.add(confirmButton, 6, 4, 5, 1)
        root.validate(this)
    }
}