package at.cath.simpletabs.mixins;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.CommandSuggestor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChatScreen.class)
public interface MixinSuggestorAccessor {
    @Accessor("commandSuggestor")
    CommandSuggestor getCommandSuggestor();
}
