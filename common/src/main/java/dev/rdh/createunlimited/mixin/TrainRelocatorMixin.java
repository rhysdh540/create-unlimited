package dev.rdh.createunlimited.mixin;


import com.simibubi.create.content.trains.entity.TrainRelocator;

import dev.rdh.createunlimited.config.CUConfigs;

import manifold.rt.api.NoBootstrap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@NoBootstrap
@Mixin(value = TrainRelocator.class, remap = false)
public abstract class TrainRelocatorMixin {
	@ModifyConstant(method = "onClicked", constant = @Constant(doubleValue = 24))
	private static double modifyMaxTrainRelocatingDistance(double original) {
		return CUConfigs.server().maxTrainRelocationDistance.get();
	}

	@ModifyConstant(method = "clientTick", constant = @Constant(doubleValue = 24))
	private static double modifyMaxTrainRelocatingDistanceClient(double original) {
		return CUConfigs.server().maxTrainRelocationDistance.get();
	}
}
