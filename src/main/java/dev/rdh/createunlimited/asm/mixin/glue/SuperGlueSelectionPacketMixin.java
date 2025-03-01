package dev.rdh.createunlimited.asm.mixin.glue;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionPacket;

import dev.rdh.createunlimited.config.CUConfig;

@Mixin(value = SuperGlueSelectionPacket.class, remap = false)
public abstract class SuperGlueSelectionPacketMixin {
	// server-side modification
	@ModifyExpressionValue(method = "handle", at = @At(value = "CONSTANT", args = "doubleValue=25.0"))
	private double modifyMaxSuperGlueDistance(double original) {
		return CUConfig.getOrDefault(CUConfig.instance.maxGlueConnectionRange, original);
	}
}
