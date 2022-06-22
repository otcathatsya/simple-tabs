package at.cath.simpletabs.config

import at.cath.simpletabs.TabsMod
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.nio.file.Files

object ConfigManager {
    private const val fileName = "config.json"
    private val configFile = File("${FabricLoader.getInstance().configDir}\\${TabsMod.MOD_ID}\\$fileName")

    private var backingApiConfig: ApiConfig? = null
    val apiConfig: ApiConfig
        get() = backingApiConfig ?: loadConfig().also { backingApiConfig = it }

    private val format = Json {
        isLenient = true
        prettyPrint = true
    }

    private fun loadConfig(): ApiConfig {
        if (Files.notExists(configFile.toPath())) {
            configFile.parentFile.mkdirs()
            save(ApiConfig())
        }
        return try {
            format.decodeFromString(configFile.readText())
        } catch (ex: Exception) {
            TabsMod.logger.error("Encountered an issue deserializing config, falling back to defaults")
            ApiConfig()
        }
    }

    private fun update() {
        backingApiConfig = loadConfig()
    }

    fun save(apiConfig: ApiConfig) {
        configFile.bufferedWriter().use { out ->
            out.write(format.encodeToString(apiConfig))
        }
        update()
    }
}