package dev.rdh.createunlimited.mixin;

import com.simibubi.create.infrastructure.config.CTrains;

import dev.rdh.createunlimited.CreateUnlimited;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CTrains.class, remap = false)
public abstract class CTrainsMixin {
	@ModifyExpressionValue(method = "<init>", at = @At(value = "CONSTANT", args = "intValue=128", ordinal = 0))
	private int modifyMaxTrackPlacementLength(int original) {
		CreateUnlimited.LOGGER.info("CTrains config override loaded (probably)");
		return Integer.MAX_VALUE;
	}
}
