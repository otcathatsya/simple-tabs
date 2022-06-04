package at.cath.simpletabs

import at.cath.simpletabs.mixins.MixinChatHudAccessor
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

object ClientTabsMod : ClientModInitializer {

    private val TABS_PATH = "${FabricLoader.getInstance().configDir}/tabs.json"

    override fun onInitializeClient() {
        val configFile = File(TABS_PATH)
        if (!configFile.createNewFile()) {
            TabsMod.logger.info("No config file found, creating new")
        }
        // replace vanilla ChatHud with custom one via mixin accessor
        ClientLifecycleEvents.CLIENT_STARTED.register(ClientStarted { client: MinecraftClient ->
            (client.inGameHud as MixinChatHudAccessor).setChatHud(
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