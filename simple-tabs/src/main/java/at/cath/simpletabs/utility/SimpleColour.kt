package at.cath.simpletabs.utility

import kotlin.math.floor

class SimpleColour(val red: Int, val green: Int, val blue: Int, val alpha: Int) {
    val packedRgb: Int

    init {
        var rgb: Int = alpha
        rgb = (rgb shl 8) + red
        rgb = (rgb shl 8) + green
        rgb = (rgb shl 8) + blue
        this.packedRgb = rgb
    }

    companion object {
        val BLACK = SimpleColour(0, 0, 0, 255)
        val WHITE = SimpleColour(255, 255, 255, 255)
    }

    fun fromPackedRgb(rgb: Int): SimpleColour {
        val alpha: Int = rgb shr 24 and 0xFF
        val red: Int = rgb shr 16 and 0xFF
        val green: Int = rgb shr 8 and 0xFF
        val blue: Int = rgb and 0xFF
        return SimpleColour(red, green, blue, alpha)
    }

    fun fade(colour: SimpleColour, percent: Float): SimpleColour =
        SimpleColour(colour.red, colour.green, colour.blue, floor(colour.alpha * percent).toInt())
}