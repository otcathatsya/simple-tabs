package at.cath.simpletabs

import at.cath.simpletabs.mixins.MixinChatHudAccessor
import at.cath.simpletabs.tabs.TabMenu
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStarted
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient

object ClientTabsMod : ClientModInitializer {

    override fun onInitializeClient() {
        // replace vanilla ChatHud with custom one via mixin accessor
        ClientLifecycleEvents.CLIENT_STARTED.register(ClientStarted { client: MinecraftClient ->
            (client.inGameHud as MixinChatHudAccessor).setChatHud(
                TabMenu(client)
            )
        })

        ClientPlayConnectionEvents.DISCONNECT.register(ClientPlayConnectionEvents.Disconnect { _, client ->
            (client.inGameHud.chatHud as? TabMenu)?.let {
            // TODO: implement save/load json etc
            //val encoded = Json.encodeToString((client.inGameHud.chatHud as TabMenu).pageTabs)
            }
        })
    }
}