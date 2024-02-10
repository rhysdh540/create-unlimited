package dev.rdh.createunlimited.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionPacket;

import dev.rdh.createunlimited.config.CUConfigs;

@Mixin(value = SuperGlueSelectionPacket.class, remap = false)
public abstract class SuperGlueSelectionPacketMixin {
	// server-side modification
	@ModifyConstant(method = "lambda$handle$0", constant = @Constant(doubleValue = 25))
	private double modifyMaxSuperGlueDistance(double original) {
		return CUConfigs.server.maxGlueConnectionRange.get();
	}
}
