package at.cath.simpletabs.mixins;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InGameHud.class)
public interface MixinHudAccessor {
    @Accessor("chatHud")
    @Mutable
    void setChatHud(ChatHud chatHud);
}
