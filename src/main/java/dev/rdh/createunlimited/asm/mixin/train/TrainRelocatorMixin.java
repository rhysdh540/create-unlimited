package dev.rdh.createunlimited.asm.mixin.train;

import com.simibubi.create.content.trains.entity.TrainRelocator;

import dev.rdh.createunlimited.config.CUConfig;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TrainRelocator.class, remap = false)
public abstract class TrainRelocatorMixin {
	@ModifyExpressionValue(method = {"onClicked", "clientTick"}, at = @At(value = "CONSTANT", args = "doubleValue=24.0"))
	private static double modifyMaxTrainRelocatingDistance(double original) {
		return CUConfig.instance.maxTrainRelocationDistance.get();
	}
}
