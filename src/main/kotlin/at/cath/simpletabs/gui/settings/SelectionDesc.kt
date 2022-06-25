package at.cath.simpletabs.gui.settings

import at.cath.simpletabs.tabs.ChatTab
import io.github.cottonmc.cotton.gui.client.CottonClientScreen
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.MinecraftClient
import net.minecraft.text.LiteralText

class SelectionDesc(tab: ChatTab) : LightweightGuiDescription() {

    private val saveSettingsEvent: Event<SaveCallback> =
        EventFactory.createArrayBacked(SaveCallback::class.java) { listeners ->
            object : SaveCallback {
                override fun canSave(): Boolean {
                    for (listener in listeners) {
                        val result = listener.canSave()
                        if (!result)
                            return false
                    }
                    return true
                }

                override fun save() {
                    for (listener in listeners) {
                        listener.save()
                    }
                }
            }
        }

    init {
        val root = WGridPanel()
        setRootPanel(root)
        root.setSize(100, 20)

        val tabWidth = (root.width * 0.9).toInt()
        val tabHeight = (root.height * 0.8).toInt()

        val updatePanel = UpdatePanel(tabWidth, tabHeight, tab)
        val designPanel = DesignPanel(tabWidth, tabHeight, tab)

        saveSettingsEvent.register(updatePanel)
        saveSettingsEvent.register(designPanel)

        val validator: (SaveCallback) -> Unit = {
            if (saveSettingsEvent.invoker().canSave()) {
                saveSettingsEvent.invoker().save()
                MinecraftClient.getInstance().openScreen(null)
            }
        }

        val designPanelDesc = SettingsDesc(designPanel, this, validator)
        val updatePanelDesc = SettingsDesc(updatePanel, this, validator)

        val updatePanelButton = WButton(LiteralText("Settings"))
        updatePanelButton.setOnClick {
            MinecraftClient.getInstance().openScreen(CottonClientScreen(updatePanelDesc))
        }
        val designPanelButton = WButton(LiteralText("Design"))
        designPanelButton.setOnClick {
            MinecraftClient.getInstance().openScreen(CottonClientScreen(designPanelDesc))
        }

        root.add(updatePanelButton, 0, 0, 5, 1)
        root.add(designPanelButton, 6, 0, 5, 1)

        root.validate(this)
    }
}