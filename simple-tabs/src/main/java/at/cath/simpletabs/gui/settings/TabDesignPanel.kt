package at.cath.simpletabs.gui.settings

import at.cath.simpletabs.tabs.ChatTab
import at.cath.simpletabs.utility.SimpleColour
import io.github.cottonmc.cotton.gui.widget.*
import io.github.cottonmc.cotton.gui.widget.data.Axis
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.minecraft.text.Text


class TabDesignPanel(root: WPanel, tab: ChatTab) : WGridPanel() {

    private val inputBackgroundColHex: WTextField
    private val bgTransparencySlider: WSlider

    private val inputOutlineColHex: WTextField
    private val outlineTransparencySlider: WSlider

    private val inputTextColHex: WTextField
    private val textTransparencySlider: WSlider

    init {
        insets = Insets.ROOT_PANEL
        setSize((root.width * 0.9).toInt(), (root.height * 0.8).toInt())

        with(tab.theme) {
            inputBackgroundColHex = WTextField()
            inputBackgroundColHex.text = backgroundColour.asHexString()

            bgTransparencySlider = WSlider(0, 255, Axis.HORIZONTAL)
            bgTransparencySlider.value = backgroundColour.alpha

            val inputBgColLabel = WDynamicColourLabel("Background Colour") {
                SimpleColour.packedFromHex(inputBackgroundColHex.text)?.packedRgb ?: 0x000000
            }

            add(inputBgColLabel, 0, 0, 4, 1)
            add(inputBackgroundColHex, 0, 1, 4, 1)
            add(bgTransparencySlider, 6, 1, 3, 1)

            inputOutlineColHex = WTextField()
            inputOutlineColHex.text = outlineColour.asHexString()

            outlineTransparencySlider = WSlider(0, 255, Axis.HORIZONTAL)
            outlineTransparencySlider.value = outlineColour.alpha

            val inputOutlineColLabel = WDynamicColourLabel("Outline Colour") {
                SimpleColour.packedFromHex(inputOutlineColHex.text)?.packedRgb ?: 0x000000
            }

            add(inputOutlineColLabel, 0, 3, 4, 1)
            add(inputOutlineColHex, 0, 4, 4, 1)
            add(outlineTransparencySlider, 6, 4, 3, 1)

            inputTextColHex = WTextField()
            inputTextColHex.text = textColour.asHexString()

            textTransparencySlider = WSlider(0, 255, Axis.HORIZONTAL)
            textTransparencySlider.value = textColour.alpha

            val inputTextColLabel = WDynamicColourLabel("Text Colour") {
                SimpleColour.packedFromHex(inputTextColHex.text)?.packedRgb ?: 0x000000
            }

            add(inputTextColLabel, 0, 6, 4, 1)
            add(inputTextColHex, 0, 7, 4, 1)
            add(textTransparencySlider, 6, 7, 3, 1)
        }

        val inputTransparencyLabel = WLabel(Text.of("Transparency"))
        add(inputTransparencyLabel, 6, 0, 3, 1)

        val button = WButton(Text.of("Confirm"))
        button.setOnClick {
            val colBackground =
                SimpleColour.packedFromHex(inputBackgroundColHex.text)?.setAlpha(bgTransparencySlider.value)
            val colOutline =
                SimpleColour.packedFromHex(inputOutlineColHex.text)?.setAlpha(outlineTransparencySlider.value)
            val colText = SimpleColour.packedFromHex(inputTextColHex.text)?.setAlpha(textTransparencySlider.value)

            tab.theme.apply {
                if (colBackground != null)
                    setBackgroundCol(colBackground)

                if (colOutline != null)
                    setOutlineCol(colOutline)

                if (colText != null)
                    setTextCol(colText)
            }
        }
        add(button, 2, 9, 5, 1)
    }
}