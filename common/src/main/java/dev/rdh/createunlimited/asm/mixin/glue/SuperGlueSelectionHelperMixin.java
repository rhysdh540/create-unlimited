package dev.rdh.createunlimited.asm.mixin.glue;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionHelper;

@Mixin(SuperGlueSelectionHelper.class)
public class SuperGlueSelectionHelperMixin {
	@ModifyExpressionValue(method = "searchGlueGroup", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/glue/SuperGlueEntity;isValidFace(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"))
	private static boolean modifyIsValidFace(boolean original) {
		return true;
	}
}
