package dev.rdh.createunlimited.asm.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import dev.rdh.createunlimited.config.CUConfig;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;

@Mixin(ArmBlockEntity.class)
public class ArmBlockEntityMixin {
	@ModifyExpressionValue(method = "tickMovementProgress", at = @At(value = "CONSTANT", args = "floatValue=256.0"))
	private float createunlimited_modifyMaxArmSpeed(float original) {
		return CUConfig.getOrDefault(CUConfig.instance.maxArmSpeed, (int) original).floatValue();
	}
}
