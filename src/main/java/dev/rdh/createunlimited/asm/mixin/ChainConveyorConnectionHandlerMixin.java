package dev.rdh.createunlimited.asm.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import dev.rdh.createunlimited.config.CUConfig;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorConnectionHandler;

// TODO: make the first and third not look buggy
@Mixin(ChainConveyorConnectionHandler.class)
public abstract class ChainConveyorConnectionHandlerMixin {

	// cannot_connect_vertically
	@ModifyExpressionValue(method = "validateAndConnect", at = @At(value = "CONSTANT", args = "doubleValue=0.0", ordinal = 1))
	private static double modifyChainConveyorConnectionHandler(double value) {
		return CUConfig.getOrTrue(CUConfig.instance.chainConveyorConnectionLimits) ? value : Double.NEGATIVE_INFINITY;
	}

	// too_steep
	@ModifyExpressionValue(method = "validateAndConnect", at = @At(value = "INVOKE", target = "Ljava/lang/Math;abs(D)D"))
	private static double modifyChainConveyorConnectionHandler2(double value) {
		return CUConfig.getOrTrue(CUConfig.instance.chainConveyorConnectionLimits) ? value : 0;
	}

	// too_close
	@ModifyExpressionValue(method = "validateAndConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;closerThan(Lnet/minecraft/core/Vec3i;D)Z", ordinal = 1))
	private static boolean modifyChainConveyorConnectionHandler3(boolean value) {
		return CUConfig.getOrTrue(CUConfig.instance.chainConveyorConnectionLimits) && value;
	}
}
