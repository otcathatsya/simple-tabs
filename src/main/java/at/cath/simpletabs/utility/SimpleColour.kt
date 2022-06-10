package at.cath.simpletabs.utility

@kotlinx.serialization.Serializable
class SimpleColour(val red: Int, val green: Int, val blue: Int, var alpha: Int) {

    val packedRgb: Int
        get() = alpha and 0xFF shl 24 or
                (red and 0xFF shl 16) or
                (green and 0xFF shl 8) or
                (blue and 0xFF)

    companion object {
        fun packedFromHex(hex: String): SimpleColour? {
            return try {
                val r = hex.substring(1, 3).toInt(16)
                val g = hex.substring(3, 5).toInt(16)
                val b = hex.substring(5, 7).toInt(16)
                SimpleColour(r, g, b, 255)
            } catch (ex: Exception) {
                null
            }
        }

        val BLACK = SimpleColour(0, 0, 0, 255)
        val WHITE = SimpleColour(255, 255, 255, 255)
        val RED = SimpleColour(237, 66, 69, 255)
    }

    fun asHexString(): String = String.format("#%02x%02x%02x", red, green, blue)

    fun fade(alpha: Int): SimpleColour = apply {
        this.alpha = alpha
    }
}