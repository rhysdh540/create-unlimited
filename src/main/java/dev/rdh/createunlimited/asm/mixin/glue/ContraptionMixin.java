package dev.rdh.createunlimited.asm.mixin.glue;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.simibubi.create.content.contraptions.Contraption;

@Mixin(Contraption.class)
public abstract class ContraptionMixin {
	@ModifyExpressionValue(method = "moveBlock", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/glue/SuperGlueEntity;isGlued(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Ljava/util/Set;)Z"))
	private boolean a(boolean original) {
		return true;
	}
}
