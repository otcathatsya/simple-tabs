package at.cath.simpletabs.mixins;

import net.minecraft.client.gui.screen.CommandSuggestor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CommandSuggestor.class)
public interface MixinSuggestorState {
    @Accessor("window")
    CommandSuggestor.SuggestionWindow getSuggestionWindow();
}
