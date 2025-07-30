package dev.rdh.createunlimited.asm.mixin;

import com.simibubi.create.content.decoration.copycat.CopycatBlock;

import dev.rdh.createunlimited.config.CUConfig;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

#if !forge @org.spongepowered.asm.mixin.Pseudo #endif
@Mixin(
	value = CopycatBlock.class
	#if !forge
	, targets = "com.copycatsplus.copycats.foundation.copycat.ICopycatBlock"
	#endif
)
public abstract class CopycatBlockMixin {
	#if !forge @org.spongepowered.asm.mixin.Dynamic("copycats plus compat") #endif
	@ModifyExpressionValue(method = "getAcceptedBlockState", at = {
		@At(value = "INVOKE", target = "Lcom/simibubi/create/content/decoration/copycat/CopycatBlock;isAcceptedRegardless(Lnet/minecraft/world/level/block/state/BlockState;)Z"),
		#if !forge
		@At(value = "INVOKE", target = "Lcom/copycatsplus/copycats/foundation/copycat/CopycatBlock;isAcceptedRegardless(Lnet/minecraft/world/level/block/state/BlockState;)Z")
		#endif
	})
	private boolean isAcceptedRegardless(boolean original) {
		return CUConfig.getOrFalse(CUConfig.instance.allowAllCopycatBlocks) || original;
	}
}
