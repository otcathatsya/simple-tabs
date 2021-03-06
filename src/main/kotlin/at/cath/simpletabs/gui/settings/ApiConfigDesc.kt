package at.cath.simpletabs.gui.settings

import at.cath.simpletabs.config.ApiConfig
import at.cath.simpletabs.config.ConfigManager
import at.cath.simpletabs.translate.RetrofitDeepl
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WLabel
import io.github.cottonmc.cotton.gui.widget.WTextField
import io.github.cottonmc.cotton.gui.widget.data.Color
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment
import io.github.cottonmc.cotton.gui.widget.data.Insets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.minecraft.text.Text

class ApiConfigDesc : LightweightGuiDescription() {

    private val config: ApiConfig

    init {
        val root = WGridPanel(22)
        setRootPanel(root)
        root.setSize(256, 80)
        root.insets = Insets.ROOT_PANEL

        config = ConfigManager.apiConfig

        val apiKeyLabel = WLabel(Text.of("Input DeepL api key for verification"))
        apiKeyLabel.horizontalAlignment = HorizontalAlignment.CENTER

        val apiKeyInput = WTextField()
        apiKeyInput.maxLength = 200
        apiKeyInput.text = config.deeplApiKey

        val confirmButton = WButton(Text.of("Validate"))
        confirmButton.setOnClick {
            val apiKey = apiKeyInput.text
            CoroutineScope(Dispatchers.IO).launch {
                val validKey = RetrofitDeepl.verifyKey(apiKey)
                if (validKey) {
                    apiKeyLabel.text = Text.of("Success! You may close this window now.")
                    apiKeyLabel.color = Color.GREEN_DYE.toRgb()
                    ConfigManager.save(config.setApiKey(apiKey))
                } else {
                    apiKeyLabel.text = Text.of("Something went wrong; is the key valid?")
                    apiKeyLabel.color = Color.RED_DYE.toRgb()
                }
            }
        }

        root.add(apiKeyLabel, 0, 0, 12, 1)
        root.add(apiKeyInput, 0, 1, 12, 1)
        root.add(confirmButton, 3, 2, 6, 1)

        root.validate(this)
    }
}