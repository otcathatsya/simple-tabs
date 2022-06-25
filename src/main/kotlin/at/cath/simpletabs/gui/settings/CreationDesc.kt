package at.cath.simpletabs.gui.settings

import at.cath.simpletabs.tabs.ChatTab
import at.cath.simpletabs.tabs.TabMenu
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.WButton
import net.minecraft.client.MinecraftClient
import net.minecraft.text.LiteralText

class CreationDesc(width: Int, height: Int, tabMenu: TabMenu) : LightweightGuiDescription() {
    init {
        val root = UpdatePanel(width, height, null)
        setRootPanel(root)

        val confirmButton = WButton(LiteralText("Confirm"))
        confirmButton.setOnClick {
            with(root) {
                if (root.canSave()) {
                    tabMenu.addTab(
                        ChatTab(
                            name = inputName.text,
                            regex = inputRegex.text,
                            inverted = toggleInverted.toggle,
                            muted = toggleMuted.toggle,
                            literal = toggleLiteral.toggle,
                            language = ChatTab.TranslationTarget(inputTargetLanguage.text),
                            prefix = inputPrefix.text
                        )
                    )
                    MinecraftClient.getInstance().currentScreen = null
                }
            }
        }
        root.add(confirmButton, 6, 4, 5, 1)
        root.validate(this)
    }
}