package dev.rdh.createunlimited.mixin;

import com.simibubi.create.content.decoration.copycat.CopycatBlock;

import dev.rdh.createunlimited.Util;
import dev.rdh.createunlimited.config.CUConfigs;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("unused")
@Mixin(value = CopycatBlock.class)
public abstract class CopycatBlockMixin {
	@ModifyExpressionValue(method = "getAcceptedBlockState", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/decoration/copycat/CopycatBlock;isAcceptedRegardless(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	private boolean isAcceptedRegardless(boolean original) {
		return Util.orFalse(CUConfigs.server.allowAllCopycatBlocks) || original;
	}
}
