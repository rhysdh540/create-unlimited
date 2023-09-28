package dev.rdh.createunlimited.mixin;


import com.simibubi.create.content.trains.entity.TrainRelocator;

import dev.rdh.createunlimited.config.CUConfigs;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(TrainRelocator.class)
public abstract class TrainRelocatorMixin {
	@ModifyConstant(method = "onClicked", constant = @Constant(doubleValue = 24), remap = false)
	private static double modifyMaxTrainRelocatingDistance(double original) {
		return CUConfigs.server().maxTrainRelocationDistance.get();
	}

	@ModifyConstant(method = "clientTick", constant = @Constant(doubleValue = 24), remap = false)
	private static double modifyMaxTrainRelocatingDistanceClient(double original) {
		return CUConfigs.server().maxTrainRelocationDistance.get();
	}
}
