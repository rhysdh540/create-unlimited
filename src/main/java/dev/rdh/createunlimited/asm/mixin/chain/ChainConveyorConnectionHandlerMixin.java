package dev.rdh.createunlimited.asm.mixin.chain;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.rdh.createunlimited.config.CUConfig;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorConnectionHandler;

import net.minecraft.world.phys.Vec3;

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
		return value && CUConfig.getOrTrue(CUConfig.instance.chainConveyorConnectionLimits);
	}

	@Redirect(method = "clientTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;cross(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"))
	private static Vec3 modifyChainConveyorClientTick(Vec3 diff, Vec3 up) {
		Vec3 horizontalDiff = diff.multiply(1, 0, 1);
		if (horizontalDiff.length() < 0.01) {
			return new Vec3(1, 0, 0);
		} else {
			return horizontalDiff.cross(up);
		}
	}
}
