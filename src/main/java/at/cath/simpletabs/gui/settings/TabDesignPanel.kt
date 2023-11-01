package at.cath.simpletabs.gui.settings

import at.cath.simpletabs.gui.WDynamicColourLabel
import at.cath.simpletabs.tabs.ChatTab
import at.cath.simpletabs.utility.SimpleColour
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WLabeledSlider
import io.github.cottonmc.cotton.gui.widget.WTextField
import io.github.cottonmc.cotton.gui.widget.data.Axis
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.minecraft.text.Text
import net.minecraft.util.ActionResult


class TabDesignPanel(width: Int, height: Int, private val tab: ChatTab) : WGridPanel(), SaveSettingsCallback {

    private lateinit var inputBackgroundColHex: WTextField
    private lateinit var bgTransparencySlider: WLabeledSlider

    private lateinit var inputOutlineColHex: WTextField
    private lateinit var outlineTransparencySlider: WLabeledSlider

    private lateinit var inputTextColHex: WTextField
    private lateinit var textTransparencySlider: WLabeledSlider

    init {
        insets = Insets.ROOT_PANEL
        setSize(width, height)
        drawInputWidgets()
    }

    private fun drawInputWidgets() {
        with(tab.theme) {
            inputBackgroundColHex = WTextField()
            inputBackgroundColHex.text = backgroundColour.asHexString()

            bgTransparencySlider = WLabeledSlider(0, 255, Axis.HORIZONTAL, Text.of("Background Transparency"))
            bgTransparencySlider.value = backgroundColour.alpha

            val inputBgColLabel = WDynamicColourLabel("Background Colour") {
                SimpleColour.packedFromHex(inputBackgroundColHex.text)?.packedRgb ?: 0x000000
            }

            add(inputBgColLabel, 0, 0, 4, 1)
            add(inputBackgroundColHex, 0, 1, 3, 1)
            add(bgTransparencySlider, 4, 1, 8, 1)

            inputOutlineColHex = WTextField()
            inputOutlineColHex.text = outlineColour.asHexString()

            outlineTransparencySlider = WLabeledSlider(0, 255, Axis.HORIZONTAL, Text.of("Outline Transparency"))
            outlineTransparencySlider.value = outlineColour.alpha

            val inputOutlineColLabel = WDynamicColourLabel("Outline Colour") {
                SimpleColour.packedFromHex(inputOutlineColHex.text)?.packedRgb ?: 0x000000
            }

            add(inputOutlineColLabel, 0, 3, 4, 1)
            add(inputOutlineColHex, 0, 4, 3, 1)
            add(outlineTransparencySlider, 4, 4, 8, 1)

            inputTextColHex = WTextField()
            inputTextColHex.text = textColour.asHexString()

            textTransparencySlider = WLabeledSlider(0, 255, Axis.HORIZONTAL, Text.of("Text Transparency"))
            textTransparencySlider.value = textColour.alpha

            val inputTextColLabel = WDynamicColourLabel("Text Colour") {
                SimpleColour.packedFromHex(inputTextColHex.text)?.packedRgb ?: 0x000000
            }

            add(inputTextColLabel, 0, 6, 4, 1)
            add(inputTextColHex, 0, 7, 3, 1)
            add(textTransparencySlider, 4, 7, 8, 1)
        }
    }

    override fun onClose(): ActionResult {
        val colBackground = SimpleColour.packedFromHex(inputBackgroundColHex.text)?.fade(bgTransparencySlider.value)
        val colOutline = SimpleColour.packedFromHex(inputOutlineColHex.text)?.fade(outlineTransparencySlider.value)
        val colText = SimpleColour.packedFromHex(inputTextColHex.text)?.fade(textTransparencySlider.value)

        return if (colBackground != null && colOutline != null && colText != null) {
            tab.theme.apply {
                setBackgroundCol(colBackground)
                setOutlineCol(colOutline)
                setTextCol(colText)
            }
            ActionResult.PASS
        } else {
            ActionResult.FAIL
        }
    }
}