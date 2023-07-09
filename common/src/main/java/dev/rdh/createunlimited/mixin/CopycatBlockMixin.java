package dev.rdh.createunlimited.mixin;

import com.simibubi.create.content.decoration.copycat.CopycatBlock;

import dev.rdh.createunlimited.config.CUConfig;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CopycatBlock.class)
public class CopycatBlockMixin {
	@Redirect(method = "getAcceptedBlockState", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/decoration/copycat/CopycatBlock;isAcceptedRegardless(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	private boolean isAcceptedRegardless(CopycatBlock instance, BlockState material) {
		return CUConfig.allowAllCopycatBlocks.get() || instance.isAcceptedRegardless(material);
	}
}
