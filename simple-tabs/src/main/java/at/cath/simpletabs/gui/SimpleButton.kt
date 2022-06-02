package at.cath.simpletabs.gui

import at.cath.simpletabs.utility.SimpleColour
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ButtonWidget.PressAction
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

open class SimpleButton(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    message: Text,
    private val backgroundColour: SimpleColour,
    private val outlineColour: SimpleColour,
    private val textColour: SimpleColour,
    private val clickCallback: MouseActionCallback = object : MouseActionCallback {}
) : ButtonWidget(x, y, width, height, message, PressAction { }) {

    override fun renderButton(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        val minecraftClient = MinecraftClient.getInstance()
        val textRenderer = minecraftClient.textRenderer
        DrawableHelper.fill(matrices, x, y, x + width, y + height, backgroundColour.packedRgb)

        drawHorizontalLine(matrices, x, x + width, y, outlineColour.packedRgb)
        drawHorizontalLine(matrices, x, x + width, y + height, outlineColour.packedRgb)

        drawVerticalLine(matrices, x, y, y + height, outlineColour.packedRgb)
        drawVerticalLine(matrices, x + width, y, y + height, outlineColour.packedRgb)

        drawCenteredText(
            matrices, textRenderer,
            message, x + width / 2, y + (height - 6) / 2, textColour.packedRgb
        )
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (clicked(mouseX, mouseY)) {
            when (button) {
                0 -> clickCallback.onLeftClick()
                1 -> clickCallback.onRightClick()
                2 -> clickCallback.onMouseMiddleClick()
                else -> return false
            }
            playDownSound(MinecraftClient.getInstance().soundManager)
            return true
        }
        return false
    }
}
