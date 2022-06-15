package at.cath.simpletabs.gui.settings

import at.cath.simpletabs.config.Config
import at.cath.simpletabs.config.ConfigManager
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

    lateinit var inputApiKey: WTextField
    lateinit var inputSourceLanguage: WTextField
    lateinit var inputTargetLanguage: WTextField

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

        inputApiKey = WTextField(Text.of("API Key"))
        inputApiKey.maxLength = 50

        val inputSourceLanguageLabel = WLabel(Text.of("Source Language"))
        val inputTargetLanguageLabel = WLabel(Text.of("Target Language"))

        inputSourceLanguage = WTextField()
        inputSourceLanguage.maxLength = 2

        inputTargetLanguage = WTextField()
        inputTargetLanguage.maxLength = 2

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

                if (language != null) {
                    inputSourceLanguage.text = language!!.sourceLanguage
                    inputTargetLanguage.text = language!!.targetLanguage
                }
            }
        }

        inputApiKey.text = ConfigManager.config.deeplApiKey

        add(inputNameLabel, 0, 0, 1, 1)
        add(inputName, 0, 1, 5, 1)

        add(inputRegexLabel, 0, 3, 1, 1)
        add(inputRegex, 0, 4, 5, 1)

        add(inputApiKey, 6, 4, 5, 1)

        add(inputSourceLanguageLabel, 0, 6, 1, 1)
        add(inputSourceLanguage, 0, 7, 2, 1)

        add(inputTargetLanguageLabel, 7, 6, 1, 1)
        add(inputTargetLanguage, 7, 7, 2, 1)

        add(toggleInverted, 6, 0, 1, 1)
        add(toggleMuted, 6, 1, 1, 1)
        add(toggleLiteral, 6, 2, 1, 1)
    }

    override fun onClose(): ActionResult {
        if (inputName.text.isNotEmpty()
            && inputRegex.text.isNotEmpty() && (toggleLiteral.toggle || try {
                inputRegex.text.toRegex()
                true
            } catch (ex: PatternSyntaxException) {
                false
            })
        ) {
            tab?.updateSettings(
                name = inputName.text, regex = inputRegex.text,
                inverted = toggleInverted.toggle, muted = toggleMuted.toggle,
                literal = toggleLiteral.toggle,
                language = if (inputTargetLanguage.text.isEmpty()) null else ChatTab.TranslationTarget(
                    inputSourceLanguage.text,
                    inputTargetLanguage.text
                )
            )

            // TODO: bad practise, don't
            ConfigManager.save(Config(inputApiKey.text))
            return ActionResult.PASS
        } else {
            return ActionResult.FAIL
        }
    }
}