package dev.rdh.createunlimited.mixin;

import com.simibubi.create.content.trains.track.TrackPlacement;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.rdh.createunlimited.Util;

import net.minecraft.core.Direction.Axis;
import net.minecraft.world.phys.Vec3;

/**
 * @see dev.rdh.createunlimited.Asm#instrumentTrackPlacement(org.objectweb.asm.tree.ClassNode) main asm hackery
 */
@Mixin(value = TrackPlacement.class, priority = 0)
public abstract class TrackPlacementMixin {

	@Redirect(method = "tryConnect", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/VecHelper;intersect(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/Direction$Axis;)[D", ordinal = 2))
	private static double[] modifyIntersect2(Vec3 p1, Vec3 p2, Vec3 r, Vec3 s, Axis plane) {
		return Util.intersect(p1, p2, r, s, plane);
	}

	@Redirect(method = "tryConnect", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/VecHelper;intersect(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/Direction$Axis;)[D", ordinal = 3))
	private static double[] modifyIntersect3(Vec3 p1, Vec3 p2, Vec3 r, Vec3 s, Axis plane) {
		return Util.intersect(p1, p2, r, s, plane);
	}
}
