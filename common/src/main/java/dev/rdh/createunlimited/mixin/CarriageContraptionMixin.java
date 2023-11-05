package dev.rdh.createunlimited.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.simibubi.create.content.trains.entity.CarriageContraption;

import dev.rdh.createunlimited.config.CUConfigs;

import manifold.rt.api.NoBootstrap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@NoBootstrap
@SuppressWarnings("unused")
@Mixin(value = CarriageContraption.class)
public abstract class CarriageContraptionMixin {
	@ModifyExpressionValue(method = "assemble", at = @At(value = "INVOKE", target = "Ljava/util/Map;size()I", ordinal = 0))
	private int size(int original) {
		return !CUConfigs.server().trainAssemblyChecks.get() ? 2 : original;
	}
}
