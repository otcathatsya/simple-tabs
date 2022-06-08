package at.cath.simpletabs.mixins;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ChatHud.class)
public class MixinChatHud {
    @Shadow
    private int scrolledLines;

    protected int getScrolledLines() {
        return scrolledLines;
    }

    @ModifyConstant(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", constant = @Constant(intValue = 100), expect = 2)
    public int expandChatHistory(int original) {
        return Integer.MAX_VALUE;
    }

    @Inject(method = "render", locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"
                    , ordinal = 0))
    public void drawWithBackground(MatrixStack matrices, int tickDelta, CallbackInfo ci, int i, int j, boolean bl, float f, int k, double d, double e, double g, double h, int l, int m, ChatHudLine<OrderedText> chatHudLine, int n, double o, int p, int q, int r, double s) {

    }
}