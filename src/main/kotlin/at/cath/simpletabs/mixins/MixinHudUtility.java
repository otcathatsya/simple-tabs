package at.cath.simpletabs.mixins;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ChatHud.class)
public interface MixinHudUtility {
    @Invoker("addMessage")
    void addMessageWithoutLog(Text message, int messageId, int timestamp, boolean refresh);

    @Accessor("visibleMessages")
    List<ChatHudLine> getVisibleMessages();

    @Accessor("messages")
    List<ChatHudLine> getLocalMessageHistory();

    @Accessor("scrolledLines")
    int getScrolledLines();

}
