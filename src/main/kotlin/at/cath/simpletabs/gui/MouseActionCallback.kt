package at.cath.simpletabs.gui

interface MouseActionCallback {
    fun onLeftClick() {}
    fun onRightClick() {}
    fun onMouseMiddleClick() {}
}

enum class MouseAction {
    LEFT_CLICK,
    RIGHT_CLICK,
    MIDDLE_MOUSE;

    companion object {
        fun create(numeric: Int): MouseAction? {
            return when (numeric) {
                0 -> LEFT_CLICK
                1 -> RIGHT_CLICK
                2 -> MIDDLE_MOUSE
                else -> null
            }
        }
    }
}