package dev.rdh.createunlimited.asm.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionPacket;

import dev.rdh.createunlimited.Util;
import dev.rdh.createunlimited.config.CUConfigs;

@Mixin(value = SuperGlueSelectionPacket.class, remap = false)
public abstract class SuperGlueSelectionPacketMixin {
	// server-side modification
	@ModifyExpressionValue(method = "lambda$handle$0", at = @At(value = "CONSTANT", args = "doubleValue=25.0"))
	private double modifyMaxSuperGlueDistance(double original) {
		return Util.orElse(CUConfigs.server.maxGlueConnectionRange, original);
	}
}
