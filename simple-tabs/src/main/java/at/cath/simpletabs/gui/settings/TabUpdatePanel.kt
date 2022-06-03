package at.cath.simpletabs.gui.settings

import at.cath.simpletabs.tabs.ChatTab
import at.cath.simpletabs.tabs.TabMenu
import io.github.cottonmc.cotton.gui.widget.*
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import java.util.regex.PatternSyntaxException

class TabUpdatePanel(width: Int, height: Int, tab: ChatTab?, tabMenu: TabMenu? = null) : WGridPanel() {

    init {
        insets = Insets.ROOT_PANEL
        setSize(width, height)

        val inputNameLabel = WLabel(Text.of("Tab Name"))
        val inputName = WTextField()

        val inputRegexLabel = WLabel(Text.of("Pattern"))
        val inputRegex = WTextField()
        inputRegex.maxLength = 200

        val toggleInverted = WToggleButton(Text.of("Inverted?"))
        val toggleMuted = WToggleButton(Text.of("Muted?"))
        val toggleLiteral = WToggleButton(Text.of("Match literal?"))

        if (tab != null) {
            toggleInverted.toggle = tab.inverted
            toggleMuted.toggle = tab.muted
            toggleLiteral.toggle = tab.literal

            inputName.text = tab.name
            inputRegex.text = tab.regex
        }

        add(inputNameLabel, 0, 0, 1, 1)
        add(inputName, 0, 1, 5, 1)

        add(inputRegexLabel, 0, 3, 1, 1)
        add(inputRegex, 0, 4, 5, 1)

        add(toggleInverted, 6, 0, 1, 1)
        add(toggleMuted, 6, 1, 1, 1)
        add(toggleLiteral, 6, 2, 1, 1)

        val button = WButton(Text.of("Confirm"))

        button.setOnClick {
            if (inputName.text.isNotEmpty() && inputRegex.text.isNotEmpty() && (toggleLiteral.toggle || try {
                    inputRegex.text.toRegex()
                    true
                } catch (ex: PatternSyntaxException) {
                    false
                })
            ) {
                if (tab != null) {
                    tab.updateSettings(
                        name = inputName.text, regex = inputRegex.text,
                        inverted = toggleInverted.toggle, muted = toggleMuted.toggle,
                        literal = toggleLiteral.toggle
                    )
                } else tabMenu?.addTab(
                    ChatTab(
                        name = inputName.text, regex = inputRegex.text,
                        inverted = toggleInverted.toggle, muted = toggleMuted.toggle,
                        literal = toggleLiteral.toggle
                    )
                )
                MinecraftClient.getInstance().setScreen(null)
            }
        }
        add(button, 6, 4, 5, 1)
    }
}