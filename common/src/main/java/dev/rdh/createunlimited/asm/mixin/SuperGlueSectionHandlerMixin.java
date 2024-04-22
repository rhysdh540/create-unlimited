package dev.rdh.createunlimited.asm.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionHandler;

import dev.rdh.createunlimited.Util;
import dev.rdh.createunlimited.config.CUConfigs;

@Mixin(value = SuperGlueSelectionHandler.class, remap = false)
public abstract class SuperGlueSectionHandlerMixin {
	// client-side modification
	@ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "doubleValue=24.0"))
	private double modifyMaxSuperGlueDistance(double original) {
		return Util.orElse(CUConfigs.server.maxGlueConnectionRange, original);
	}
}
