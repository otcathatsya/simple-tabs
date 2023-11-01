package at.cath.simpletabs.mixins;

import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChatScreen.class)
public class MixinSuggestorAccessor {
    @Shadow
    ChatInputSuggestor chatInputSuggestor;

    @Unique
    public ChatInputSuggestor getChatInputSuggestor() {
        return chatInputSuggestor;
    }
}
