package dev.rdh.createunlimited.asm.mixin;

import com.simibubi.create.content.decoration.copycat.CopycatBlock;

import dev.rdh.createunlimited.config.CUConfig;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("unused")
@Pseudo
@Mixin(value = CopycatBlock.class, targets = "com.copycatsplus.copycats.foundation.copycat.ICopycatBlock")
public abstract class CopycatBlockMixin {
	@Dynamic("copycats plus compat")
	@ModifyExpressionValue(method = "getAcceptedBlockState", at = {
		@At(value = "INVOKE", target = "Lcom/simibubi/create/content/decoration/copycat/CopycatBlock;isAcceptedRegardless(Lnet/minecraft/world/level/block/state/BlockState;)Z"),
		@At(value = "INVOKE", target = "Lcom/copycatsplus/copycats/foundation/copycat/CopycatBlock;isAcceptedRegardless(Lnet/minecraft/world/level/block/state/BlockState;)Z")
	})
	private boolean isAcceptedRegardless(boolean original) {
		return CUConfig.getOrFalse(CUConfig.instance.allowAllCopycatBlocks) || original;
	}
}
