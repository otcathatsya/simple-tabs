package at.cath.simpletabs.gui.settings

import at.cath.simpletabs.gui.WColourLabel
import at.cath.simpletabs.tabs.ChatTab
import at.cath.simpletabs.utility.SimpleColour
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WLabeledSlider
import io.github.cottonmc.cotton.gui.widget.WTextField
import io.github.cottonmc.cotton.gui.widget.data.Axis
import net.minecraft.text.LiteralText


class DesignPanel(width: Int, height: Int, private val tab: ChatTab) : WGridPanel(), SaveCallback {

    private lateinit var inputBackgroundColHex: WTextField
    private lateinit var bgTransparencySlider: WLabeledSlider

    private lateinit var inputOutlineColHex: WTextField
    private lateinit var outlineTransparencySlider: WLabeledSlider

    private lateinit var inputTextColHex: WTextField
    private lateinit var textTransparencySlider: WLabeledSlider

    init {
        setSize(width, height)
        drawInputWidgets()
    }

    private fun drawInputWidgets() {
        with(tab.theme) {
            inputBackgroundColHex = WTextField()
            inputBackgroundColHex.text = backgroundColour.asHexString()

            bgTransparencySlider = WLabeledSlider(0, 255, Axis.HORIZONTAL, LiteralText("Background Transparency"))
            bgTransparencySlider.value = backgroundColour.alpha

            val inputBgColLabel = WColourLabel("Background Colour") {
                SimpleColour.packedFromHex(inputBackgroundColHex.text)?.packedRgb ?: 0x000000
            }

            add(inputBgColLabel, 0, 0, 4, 1)
            add(inputBackgroundColHex, 0, 1, 3, 1)
            add(bgTransparencySlider, 4, 1, 8, 1)

            inputOutlineColHex = WTextField()
            inputOutlineColHex.text = outlineColour.asHexString()

            outlineTransparencySlider = WLabeledSlider(0, 255, Axis.HORIZONTAL, LiteralText("Outline Transparency"))
            outlineTransparencySlider.value = outlineColour.alpha

            val inputOutlineColLabel = WColourLabel("Outline Colour") {
                SimpleColour.packedFromHex(inputOutlineColHex.text)?.packedRgb ?: 0x000000
            }

            add(inputOutlineColLabel, 0, 3, 4, 1)
            add(inputOutlineColHex, 0, 4, 3, 1)
            add(outlineTransparencySlider, 4, 4, 8, 1)

            inputTextColHex = WTextField()
            inputTextColHex.text = textColour.asHexString()

            textTransparencySlider = WLabeledSlider(0, 255, Axis.HORIZONTAL, LiteralText("Text Transparency"))
            textTransparencySlider.value = textColour.alpha

            val inputTextColLabel = WColourLabel("Text Colour") {
                SimpleColour.packedFromHex(inputTextColHex.text)?.packedRgb ?: 0x000000
            }

            add(inputTextColLabel, 0, 6, 4, 1)
            add(inputTextColHex, 0, 7, 3, 1)
            add(textTransparencySlider, 4, 7, 8, 1)
        }
    }

    override fun canSave(): Boolean {
        println("DESIGN CAN SAVE?")
        val colBackground = SimpleColour.packedFromHex(inputBackgroundColHex.text)?.fade(bgTransparencySlider.value)
        val colOutline = SimpleColour.packedFromHex(inputOutlineColHex.text)?.fade(outlineTransparencySlider.value)
        val colText = SimpleColour.packedFromHex(inputTextColHex.text)?.fade(textTransparencySlider.value)

        return colBackground != null && colOutline != null && colText != null
    }

    override fun save() {
        println("DESIGN SAVE!")
        val colBackground = SimpleColour.packedFromHex(inputBackgroundColHex.text)!!.fade(bgTransparencySlider.value)
        val colOutline = SimpleColour.packedFromHex(inputOutlineColHex.text)!!.fade(outlineTransparencySlider.value)
        val colText = SimpleColour.packedFromHex(inputTextColHex.text)!!.fade(textTransparencySlider.value)

        tab.theme.apply {
            setBackgroundCol(colBackground)
            setOutlineCol(colOutline)
            setTextCol(colText)
        }
    }
}