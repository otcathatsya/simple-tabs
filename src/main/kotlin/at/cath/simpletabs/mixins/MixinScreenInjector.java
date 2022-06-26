package at.cath.simpletabs.mixins;

import at.cath.simpletabs.gui.TabScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public class MixinScreenInjector {
    @Redirect(method = "handleInputEvents()V", at = @At(value = "NEW", target = "net/minecraft/client/gui/screen/ChatScreen"))
    ChatScreen setScreen(String originalChatText) {
        return new TabScreen(originalChatText);
    }
}