package dev.rdh.createunlimited.mixin;


import com.simibubi.create.content.decoration.copycat.CopycatBlock;

import dev.rdh.createunlimited.config.CUConfigs;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CopycatBlock.class)
@SuppressWarnings("unused")
public abstract class CopycatBlockMixin {
	@WrapOperation(method = "getAcceptedBlockState", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/decoration/copycat/CopycatBlock;isAcceptedRegardless(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	private boolean isAcceptedRegardless(boolean original) {
		return CUConfigs.server().allowAllCopycatBlocks.get() || original;
	}
}
