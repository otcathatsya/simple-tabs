package at.cath.simpletabs.gui

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ButtonWidget.PressAction
import net.minecraft.text.Text

class TabButton(
    x: Int, y: Int, width: Int, height: Int, message: Text, private val cb: MouseActionCallback
) : ButtonWidget(x, y, width, height, message, PressAction { }) {

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (clicked(mouseX, mouseY)) {
            when (button) {
                0 -> cb.onLeftClick()
                1 -> cb.onRightClick()
                2 -> cb.onMouseMiddleClick()
                else -> return false
            }
            playDownSound(MinecraftClient.getInstance().soundManager)
            return true
        }
        return false
    }
}
