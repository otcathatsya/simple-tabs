package at.cath.simpletabs.mixins;

import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChatHud.class)
public class MixinInfiniteHistory {
    // todo: make work
    @ModifyConstant(method = "addMessage(Lnet/minecraft/text/Text;)V", constant = @Constant(intValue = 100), expect = 2)
    public int expandChatHistory(int original) {
        return Integer.MAX_VALUE;
    }
}