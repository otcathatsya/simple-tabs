package at.cath.simpletabs.gui.settings

import io.github.cottonmc.cotton.gui.client.ScreenDrawing
import io.github.cottonmc.cotton.gui.widget.WWidget
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment
import net.minecraft.client.util.math.MatrixStack
import java.util.function.Supplier

class WDynamicColourLabel(val text: String, private val colourProvider: Supplier<Int>) : WWidget() {
    private val alignment = HorizontalAlignment.LEFT

    override fun canResize(): Boolean = true

    override fun paint(matrices: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        ScreenDrawing.drawString(
            matrices,
            text,
            alignment,
            x,
            y,
            getWidth(),
            colourProvider.get()
        )
    }
}