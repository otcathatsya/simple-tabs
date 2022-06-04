package at.cath.simpletabs.tabs

import at.cath.simpletabs.utility.SimpleColour

val CONTROL_ELEMENT = TabTheme(SimpleColour(166, 159, 152, 255), SimpleColour.WHITE, SimpleColour.BLACK)

@kotlinx.serialization.Serializable
class TabTheme(
    var backgroundColour: SimpleColour,
    var textColour: SimpleColour,
    var outlineColour: SimpleColour
) {
    fun setBackgroundCol(backgroundColour: SimpleColour) = apply { this.backgroundColour = backgroundColour }
    fun setOutlineCol(outlineColour: SimpleColour) = apply { this.outlineColour = outlineColour }
    fun setTextCol(textColour: SimpleColour) = apply { this.textColour = textColour }
}