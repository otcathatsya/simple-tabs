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
    private val configFile = File("${FabricLoader.getInstance().configDir}/${TabsMod.MOD_ID}/$fileName")

    private var backingConfig: Config? = null
    val config: Config
        get() = backingConfig ?: loadConfig().also { backingConfig = it }

    private val format = Json {
        isLenient = true
        prettyPrint = true
    }

    private fun loadConfig(): Config {
        if (Files.notExists(configFile.toPath())) {
            save(Config())
        }
        return try {
            format.decodeFromString(configFile.readText())
        } catch (ex: Exception) {
            TabsMod.logger.error("Encountered an issue deserializing config, falling back to defaults")
            Config()
        }
    }

    fun update() {
        backingConfig = loadConfig()
    }

    fun save(config: Config) {
        configFile.bufferedWriter().use { out ->
            out.write(format.encodeToString(config))
        }
        update()
    }
}