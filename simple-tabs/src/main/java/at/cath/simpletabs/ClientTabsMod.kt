package at.cath.simpletabs

import at.cath.simpletabs.mixins.MixinChatHudAccessor
import at.cath.simpletabs.tabs.ChatMenu
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStarted
import net.minecraft.client.MinecraftClient

object ClientTabsMod : ClientModInitializer {

    override fun onInitializeClient() {
        // replace vanilla ChatHud with custom one via mixin accessor
        ClientLifecycleEvents.CLIENT_STARTED.register(ClientStarted { client: MinecraftClient ->
            (client.inGameHud as MixinChatHudAccessor).setChatHud(ChatMenu(client))
        })
    }
}