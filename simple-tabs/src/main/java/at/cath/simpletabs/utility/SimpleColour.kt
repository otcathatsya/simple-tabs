package at.cath.simpletabs.utility

import kotlin.math.floor

@kotlinx.serialization.Serializable
class SimpleColour(val red: Int, val green: Int, val blue: Int, val alpha: Int) {
    val packedRgb: Int

    init {
        var rgb: Int = alpha - 1
        rgb = (rgb shl 8) + red
        rgb = (rgb shl 8) + green
        rgb = (rgb shl 8) + blue
        this.packedRgb = rgb
    }

    companion object {
        fun fromPackedRgb(rgb: Int): SimpleColour {
            val alpha: Int = rgb shr 24 and 0xFF
            val red: Int = rgb shr 16 and 0xFF
            val green: Int = rgb shr 8 and 0xFF
            val blue: Int = rgb and 0xFF
            return SimpleColour(red, green, blue, alpha)
        }

        fun packedFromHex(hex: String): Int {
            return try {
                val r = hex.substring(1, 3).toInt(16)
                val g = hex.substring(3, 5).toInt(16)
                val b = hex.substring(5, 7).toInt(16)
                SimpleColour(r, g, b, 255).packedRgb
            } catch (ex: Exception) {
                -1
            }
        }

        val BLACK = SimpleColour(0, 0, 0, 255)
        val WHITE = SimpleColour(255, 255, 255, 255)
        val RED = SimpleColour(237, 66, 69, 255)
    }

    fun asHexString(): String = String.format("#%02x%02x%02x", red, green, blue)

    fun fade(colour: SimpleColour, percent: Float): SimpleColour =
        SimpleColour(colour.red, colour.green, colour.blue, floor(colour.alpha * percent).toInt())
}