package at.cath.simpletabs.mixins;


import at.cath.simpletabs.tabs.ChatTab;
import at.cath.simpletabs.tabs.TabMenu;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinPrefixChat {

    @Shadow
    @Final
    protected MinecraftClient client;

    @Inject(method = "sendChatMessage", at = @At(value = "HEAD"), cancellable = true)
    private void prefixChatMessage(String message, CallbackInfo ci) {
        ci.cancel();
        String prefix = "";
        ChatHud chatHud = client.inGameHud.getChatHud();
        if (chatHud instanceof TabMenu) {
            ChatTab selectedTab = ((TabMenu) chatHud).getSelectedTab();
            if (selectedTab != null) {
                prefix = selectedTab.getPrefix();
            }
        }
        ((ClientPlayerEntity) (Object) this).networkHandler.sendPacket(new ChatMessageC2SPacket(prefix + message));
    }
}