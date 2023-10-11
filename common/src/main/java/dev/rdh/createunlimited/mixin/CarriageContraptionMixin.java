package dev.rdh.createunlimited.mixin;


import com.simibubi.create.content.trains.entity.CarriageContraption;

import dev.rdh.createunlimited.config.CUConfigs;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(value = CarriageContraption.class)
@SuppressWarnings("unused")
public abstract class CarriageContraptionMixin {
	@WrapOperation(method = "assemble", at = @At(value = "INVOKE", target = "Ljava/util/Map;size()I", ordinal = 0))
	private int size(int original) {
		return !CUConfigs.server().trainAssemblyChecks.get() ? 2 : original;
	}
}
