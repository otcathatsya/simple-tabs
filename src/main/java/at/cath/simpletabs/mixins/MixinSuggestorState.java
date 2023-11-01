package at.cath.simpletabs.mixins;

import net.minecraft.client.gui.screen.ChatInputSuggestor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChatInputSuggestor.class)
public interface MixinSuggestorState {

    @Accessor("window")
    ChatInputSuggestor.SuggestionWindow getSuggestionWindow();
}
