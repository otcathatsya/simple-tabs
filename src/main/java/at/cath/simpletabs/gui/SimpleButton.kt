package at.cath.simpletabs.gui

import at.cath.simpletabs.utility.SimpleColour
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ButtonWidget.PressAction
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
) : ButtonWidget(x, y, width, height, message, PressAction { }, null) {

    override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val minecraftClient = MinecraftClient.getInstance()
        val textRenderer = minecraftClient.textRenderer

        context.fill(x, y, x + width, y + height, backgroundColour.packedRgb)

        context.drawHorizontalLine(x, x + width, y, outlineColour.packedRgb)
        context.drawHorizontalLine(x, x + width, y + height, outlineColour.packedRgb)

        context.drawVerticalLine(x, y, y + height, outlineColour.packedRgb)
        context.drawVerticalLine(x + width, y, y + height, outlineColour.packedRgb)

        context.drawCenteredTextWithShadow(
            textRenderer,
            message, x + width / 2, y + (height - 6) / 2, textColour.packedRgb
        )
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (clicked(mouseX, mouseY)) {
            val action = MouseAction.create(button) ?: return false
            when (action) {
                MouseAction.LEFT_CLICK -> clickCallback.onLeftClick()
                MouseAction.RIGHT_CLICK -> clickCallback.onRightClick()
                MouseAction.MIDDLE_MOUSE -> clickCallback.onMouseMiddleClick()
            }
            playDownSound(MinecraftClient.getInstance().soundManager)
            return true
        }
        return false
    }
}
