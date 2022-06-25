package at.cath.simpletabs.gui

import at.cath.simpletabs.mixins.MixinSuggestorAccessor
import at.cath.simpletabs.mixins.MixinSuggestorState
import at.cath.simpletabs.tabs.CONTROL_ELEMENT
import at.cath.simpletabs.tabs.ChatTab
import at.cath.simpletabs.utility.SimpleColour
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.text.Text

class TabButton(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    message: Text,
    private val tab: ChatTab? = null,
    clickCallback: MouseActionCallback = object : MouseActionCallback {}
) :
    SimpleButton(
        x,
        y,
        width,
        height,
        message,
        tab?.theme?.backgroundColour ?: CONTROL_ELEMENT.backgroundColour,
        tab?.theme?.outlineColour ?: CONTROL_ELEMENT.outlineColour,
        tab?.theme?.textColour ?: CONTROL_ELEMENT.textColour,
        clickCallback
    ), TabComponent {


    override fun renderButton(mouseX: Int, mouseY: Int, delta: Float) {
        val minecraftClient = MinecraftClient.getInstance()
        val screen = minecraftClient.currentScreen
        if (screen is ChatScreen) {
            if (((screen as MixinSuggestorAccessor).commandSuggestor as MixinSuggestorState).suggestionWindow == null) {
                super.renderButton(mouseX, mouseY, delta)
                val textRenderer = minecraftClient.textRenderer
                if (tab != null) {
                    if (tab.unreadCount > 0) {
                        val startX = x + width - 4
                        val startY = y + 4

                        fill(
                            startX,
                            startY,
                            startX + 6,
                            startY - 6,
                            SimpleColour.RED.packedRgb
                        )

                        val startXScaled = startX / 0.5
                        val startYScaled = startY / 0.5

                        RenderSystem.pushMatrix()
                        RenderSystem.scaled(0.5, 0.5, 1.0)

                        drawCenteredString(
                            textRenderer,
                            "${tab.unreadCount}",
                            startXScaled.toInt() + 6,
                            startYScaled.toInt() - 9,
                            SimpleColour.WHITE.packedRgb
                        )
                        RenderSystem.popMatrix()
                    }
                }
            }
        }
    }
}