package at.cath.simpletabs.tabs

import at.cath.simpletabs.utility.SimpleColour

@kotlinx.serialization.Serializable
data class TabTheme(
    val backgroundColour: SimpleColour,
    val textColour: SimpleColour,
    val outlineColour: SimpleColour
) {
    companion object {
        val TAB_ELEMENT = TabTheme(SimpleColour(166, 159, 152, 255 / 2), SimpleColour.WHITE, SimpleColour.BLACK)
        val CONTROL_ELEMENT = TabTheme(SimpleColour(166, 159, 152, 255), SimpleColour.WHITE, SimpleColour.BLACK)
    }
}