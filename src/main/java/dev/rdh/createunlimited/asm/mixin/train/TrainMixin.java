package dev.rdh.createunlimited.asm.mixin.train;

import com.simibubi.create.content.trains.entity.Train;

import dev.rdh.createunlimited.config.CUConfigs;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Train.class)
public abstract class TrainMixin {
	@ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "doubleValue=4.0"))
	private double modifyMaxStress(double original) {
		double a = CUConfigs.getOrDefault(CUConfigs.server.maxAllowedStress, original);
		return a < 0 ? Double.MAX_VALUE : a;
	}
}
