package dev.rdh.createunlimited.asm.mixin.chain;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorVisual;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

@Mixin(ChainConveyorVisual.class)
public abstract class ChainConveyorVisualMixin extends SingleAxisRotatingVisual<ChainConveyorBlockEntity> {
	@SuppressWarnings("ALL")
	private ChainConveyorVisualMixin() {super(null, null, 0, null);}

	@ModifyExpressionValue(method = "setupGuards", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;subtract(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"))
	private Vec3 modifySetupGuardsVec3Subtract(Vec3 diff, @Local(name = "blockPos") BlockPos relativeTargetPos) {
		return new Vec3(relativeTargetPos.getX(), 0, relativeTargetPos.getZ());
	}

	@ModifyArg(method = "setupGuards", at = @At(value = "INVOKE", target = "Ldev/engine_room/flywheel/lib/instance/TransformedInstance;rotateYDegrees(F)Ldev/engine_room/flywheel/lib/transform/Rotate;"))
	private float modifySetupGuardsRotateYDegrees(float yRot, @Local(name = "blockPos") BlockPos relativeTargetPos) {
		if (relativeTargetPos.getX() == 0 && relativeTargetPos.getZ() == 0) {
			return blockEntity.getSpeed() < 0 ? 90 : -90;
		}
		return yRot;
	}
}