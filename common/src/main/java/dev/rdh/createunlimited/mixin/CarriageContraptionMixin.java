package dev.rdh.createunlimited.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.simibubi.create.content.trains.entity.CarriageContraption;

import dev.rdh.createunlimited.Util;
import dev.rdh.createunlimited.config.CUConfigs;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("unused")
@Mixin(value = CarriageContraption.class)
public abstract class CarriageContraptionMixin {
	@ModifyExpressionValue(method = "assemble", at = @At(value = "INVOKE", target = "Ljava/util/Map;size()I", ordinal = 0))
	private int modifyMinBlocksOnTrain(int original) {
		return !Util.orTrue(CUConfigs.server.trainAssemblyChecks) ? 2 : original;
	}
}
