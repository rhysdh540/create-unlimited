package dev.rdh.createunlimited.asm.mixin;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import dev.rdh.createunlimited.config.CUConfig;

import com.simibubi.create.content.kinetics.RotationPropagator;

@Mixin(RotationPropagator.class)
public class RotationPropagatorMixin {
	@ModifyExpressionValue(method = "propagateNewSource", at = @At(value = "CONSTANT", args = "intValue=128"))
	private static int modifyMaxFlickerScore(int original) {
		int configuredValue = CUConfig.getOrDefault(CUConfig.instance.maxFlickerScore, original);
		return configuredValue == 0 ? Integer.MAX_VALUE : configuredValue;
	}
}
