package dev.rdh.createunlimited.asm.mixin.chain_conveyor;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import dev.rdh.createunlimited.config.CUConfig;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorConnectionHandler;

@Mixin(ChainConveyorConnectionHandler.class)
public class ChainConveyorConnectionHandlerMixin {
	@ModifyExpressionValue(method = "validateAndConnect", at = @At(value = "CONSTANT", args = "doubleValue=0.0", ordinal = 1))
	private static double modifyChainConveyorConnectionHandler(double value) {
		if (!CUConfig.getOrTrue(CUConfig.instance.chainConveyorConnectionLimits)) {
			return Double.NEGATIVE_INFINITY;
		} else {
			return value;
		}
	}

	@ModifyExpressionValue(method = "validateAndConnect", at = @At(value = "INVOKE", target = "Ljava/lang/Math;abs(D)D"))
	private static double modifyChainConveyorConnectionHandler2(double value) {
		if (!CUConfig.getOrTrue(CUConfig.instance.chainConveyorConnectionLimits)) {
			return 0;
		} else {
			return value;
		}
	}

	@ModifyExpressionValue(method = "validateAndConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;closerThan(Lnet/minecraft/core/Vec3i;D)Z", ordinal = 1))
	private static boolean modifyChainConveyorConnectionHandler3(boolean value) {
		if (!CUConfig.getOrTrue(CUConfig.instance.chainConveyorConnectionLimits)) {
			return false;
		} else {
			return value;
		}
	}
}
