package at.cath.simpletabs.gui.settings

import at.cath.simpletabs.tabs.ChatTab
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WLabel
import io.github.cottonmc.cotton.gui.widget.WTextField
import io.github.cottonmc.cotton.gui.widget.WToggleButton
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import java.util.regex.PatternSyntaxException

class TabUpdatePanel(width: Int, height: Int, private val tab: ChatTab?) :
    WGridPanel(),
    SaveSettingsCallback {

    lateinit var inputName: WTextField
    lateinit var inputRegex: WTextField
    lateinit var toggleInverted: WToggleButton
    lateinit var toggleMuted: WToggleButton
    lateinit var toggleLiteral: WToggleButton

    init {
        insets = Insets.ROOT_PANEL
        setSize(width, height)
        drawInputWidgets()
    }

    private fun drawInputWidgets() {
        val inputNameLabel = WLabel(Text.of("Tab Name"))
        inputName = WTextField()

        val inputRegexLabel = WLabel(Text.of("Pattern"))
        inputRegex = WTextField()
        inputRegex.maxLength = 200

        toggleInverted = WToggleButton(Text.of("Inverted?"))
        toggleMuted = WToggleButton(Text.of("Muted?"))
        toggleLiteral = WToggleButton(Text.of("Match literal?"))

        with(tab) {
            if (this != null) {
                toggleInverted.toggle = inverted
                toggleMuted.toggle = muted
                toggleLiteral.toggle = literal

                inputName.text = name
                inputRegex.text = regex
            }
        }

        add(inputNameLabel, 0, 0, 1, 1)
        add(inputName, 0, 1, 5, 1)

        add(inputRegexLabel, 0, 3, 1, 1)
        add(inputRegex, 0, 4, 5, 1)

        add(toggleInverted, 6, 0, 1, 1)
        add(toggleMuted, 6, 1, 1, 1)
        add(toggleLiteral, 6, 2, 1, 1)
    }

    override fun onClose(): ActionResult {
        if (inputName.text.isNotEmpty() && inputRegex.text.isNotEmpty() && (toggleLiteral.toggle || try {
                inputRegex.text.toRegex()
                true
            } catch (ex: PatternSyntaxException) {
                false
            })
        ) {
            tab?.updateSettings(
                name = inputName.text, regex = inputRegex.text,
                inverted = toggleInverted.toggle, muted = toggleMuted.toggle,
                literal = toggleLiteral.toggle
            )
            return ActionResult.PASS
        } else {
            return ActionResult.FAIL
        }
    }
}