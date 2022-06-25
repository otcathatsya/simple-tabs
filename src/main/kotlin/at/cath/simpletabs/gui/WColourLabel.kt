package at.cath.simpletabs.gui

import io.github.cottonmc.cotton.gui.client.ScreenDrawing
import io.github.cottonmc.cotton.gui.widget.WWidget
import io.github.cottonmc.cotton.gui.widget.data.Alignment
import java.util.function.Supplier

class WColourLabel(val text: String, private val colourProvider: Supplier<Int>) : WWidget() {
    private val alignment = Alignment.LEFT

    override fun canResize(): Boolean = true

    override fun paintBackground(x: Int, y: Int) {
        ScreenDrawing.drawString(
            text,
            alignment,
            x,
            y,
            getWidth(),
            colourProvider.get()
        )
    }
}