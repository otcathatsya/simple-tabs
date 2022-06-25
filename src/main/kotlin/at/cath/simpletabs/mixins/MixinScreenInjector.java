package at.cath.simpletabs.mixins;

import at.cath.simpletabs.gui.TabScreen;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinScreenInjector {
    @Inject(method = "handleInputEvents", expect = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;<init>(Ljava/lang/String;)V"), cancellable = true)
    private void setScreen(CallbackInfo ci) {
        ci.cancel();
        ((MinecraftClient) (Object) this).openScreen(new TabScreen(""));
    }
}