package at.cath.simpletabs.mixins;

import at.cath.simpletabs.gui.ChatTabScreen;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinChatScreen {
    @Inject(
            method = "openChatScreen",
            at = @At("TAIL"))
    private void setScreen(String text, CallbackInfo ci) {
        ((MinecraftClient) (Object) this).setScreen(new ChatTabScreen(text));
    }
}
