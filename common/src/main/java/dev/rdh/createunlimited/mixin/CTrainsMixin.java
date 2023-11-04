package dev.rdh.createunlimited.mixin;


import com.simibubi.create.infrastructure.config.CTrains;

import dev.rdh.createunlimited.CreateUnlimited;

import manifold.rt.api.NoBootstrap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@NoBootstrap
@Mixin(value = CTrains.class, remap = false)
public abstract class CTrainsMixin {
	@ModifyConstant(method = "<init>", constant = @Constant(intValue = 128, ordinal = 0))
	private int modifyMaxTrackPlacementLength(int original) {
		CreateUnlimited.LOGGER.info("CTrains config override loaded (probably)");
		return Integer.MAX_VALUE;
	}
}
