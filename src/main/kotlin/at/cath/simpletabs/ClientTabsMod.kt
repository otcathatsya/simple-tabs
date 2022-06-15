package at.cath.simpletabs

import at.cath.simpletabs.mixins.MixinHudAccessor
import at.cath.simpletabs.tabs.TabMenu
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStarted
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStopping
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.createDirectories


object ClientTabsMod : ClientModInitializer {

    private val TABS_PATH = "${FabricLoader.getInstance().configDir}/${TabsMod.MOD_ID}/tabs.json"

    override fun onInitializeClient() {
        val configFile = File(TABS_PATH)
        if (!configFile.exists()) {
            Path(TABS_PATH).createDirectories()
            configFile.createNewFile()
        }
        // replace vanilla ChatHud with custom one via mixin accessor
        ClientLifecycleEvents.CLIENT_STARTED.register(ClientStarted { client: MinecraftClient ->
            (client.inGameHud as MixinHudAccessor).setChatHud(
                TabMenu(client, configFile.readText())
            )
        })

        ClientLifecycleEvents.CLIENT_STOPPING.register(ClientStopping {
            (it.inGameHud.chatHud as? TabMenu)?.let { tabMenu ->
                val tabsEncoded = Json.encodeToString(tabMenu.pageTabs)
                configFile.bufferedWriter().use { out ->
                    out.write(tabsEncoded)
                }
            }
        })
    }
}