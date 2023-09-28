package dev.rdh.createunlimited.mixin;

import com.simibubi.create.content.contraptions.BlockMovementChecks;

import dev.rdh.createunlimited.config.CUConfigs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockMovementChecks.class, remap = false)
public abstract class BlockMovementChecksMixin {
//	@Inject(method = "isNotSupportive", at = @At("HEAD"), cancellable = true)
//	private static void everythingIsSupportive(BlockState state, Direction facing, CallbackInfoReturnable<Boolean> cir) {
//		if(!CUConfigs.server().physicalBlockConnection.get())
//			cir.setReturnValue(false);
//	}
}
