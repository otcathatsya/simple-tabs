package at.cath.simpletabs.gui.settings

import at.cath.simpletabs.config.ApiConfig
import at.cath.simpletabs.config.ConfigManager
import at.cath.simpletabs.translate.RetrofitDeepl
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WLabel
import io.github.cottonmc.cotton.gui.widget.WTextField
import io.github.cottonmc.cotton.gui.widget.data.Alignment
import io.github.cottonmc.cotton.gui.widget.data.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.minecraft.text.LiteralText

class ConfigDesc : LightweightGuiDescription() {

    private val config: ApiConfig

    init {
        val root = WGridPanel(22)
        setRootPanel(root)
        root.setSize(256, 80)

        config = ConfigManager.apiConfig

        val apiKeyLabel = WLabel(LiteralText("Input DeepL api key for verification"))
        apiKeyLabel.setAlignment(Alignment.CENTER)

        val apiKeyInput = WTextField()
        apiKeyInput.maxLength = 200
        apiKeyInput.text = config.deeplApiKey

        val confirmButton = WButton(LiteralText("Validate"))
        confirmButton.setOnClick {
            val apiKey = apiKeyInput.text
            CoroutineScope(Dispatchers.IO).launch {
                val validKey = RetrofitDeepl.verifyKey(apiKey)
                if (validKey) {
                    apiKeyLabel.setText(LiteralText("Success! You may close this window now."))
                    apiKeyLabel.setColor(Color.GREEN_DYE.toRgb(), Color.GREEN_DYE.toRgb())
                    ConfigManager.save(config.setApiKey(apiKey))
                } else {
                    apiKeyLabel.setText(LiteralText("Something went wrong; is the key valid?"))
                    apiKeyLabel.setColor(Color.RED_DYE.toRgb(), Color.RED_DYE.toRgb())
                }
            }
        }

        root.add(apiKeyLabel, 0, 0, 12, 1)
        root.add(apiKeyInput, 0, 1, 12, 1)
        root.add(confirmButton, 3, 2, 6, 1)

        root.validate(this)
    }
}