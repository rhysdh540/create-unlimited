package dev.rdh.createunlimited.asm.mixin.train;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.simibubi.create.content.trains.entity.CarriageContraption;

import dev.rdh.createunlimited.config.CUConfig;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("unused")
@Mixin(CarriageContraption.class)
public abstract class CarriageContraptionMixin {
	@ModifyExpressionValue(method = "assemble", at = @At(value = "INVOKE", target = "Ljava/util/Map;size()I", ordinal = 0))
	private int modifyMinBlocksOnTrain(int original) {
		return !CUConfig.getOrTrue(CUConfig.instance.trainAssemblyChecks) ? 2 : original;
	}
}
