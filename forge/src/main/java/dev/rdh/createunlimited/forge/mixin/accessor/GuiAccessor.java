package dev.rdh.createunlimited.forge.mixin.accessor;

import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Gui.class)
public interface GuiAccessor {
	@Accessor("toolHighlightTimer")
	MutableComponent getToolHighlightTimer();
}
