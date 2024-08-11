package dev.rdh.createunlimited.asm.mixin.train;

import com.simibubi.create.content.trains.track.TrackPlacement;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.rdh.createunlimited.asm.Asm;

import net.minecraft.core.Direction.Axis;
import net.minecraft.world.phys.Vec3;

/**
 * @see Asm#instrumentTrackPlacement(org.objectweb.asm.tree.ClassNode) main asm hackery
 */
@Mixin(value = TrackPlacement.class, priority = 0)
public abstract class TrackPlacementMixin {

	@Redirect(method = "tryConnect", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/VecHelper;intersect(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/Direction$Axis;)[D", ordinal = 2))
	private static double[] modifyIntersect2(Vec3 p1, Vec3 p2, Vec3 r, Vec3 s, Axis plane) {
		return cu$intersect(p1, p2, r, s, plane);
	}

	@Redirect(method = "tryConnect", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/VecHelper;intersect(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/Direction$Axis;)[D", ordinal = 3))
	private static double[] modifyIntersect3(Vec3 p1, Vec3 p2, Vec3 r, Vec3 s, Axis plane) {
		return cu$intersect(p1, p2, r, s, plane);
	}

	@Unique
	@SuppressWarnings("SuspiciousNameCombination")
	private static double[] cu$intersect(Vec3 p1, Vec3 p2, Vec3 r, Vec3 s, Axis plane) {
		if (plane == Axis.X) {
			p1 = new Vec3(p1.y, 0, p1.z);
			p2 = new Vec3(p2.y, 0, p2.z);
			r = new Vec3(r.y, 0, r.z);
			s = new Vec3(s.y, 0, s.z);
		}

		if (plane == Axis.Z) {
			p1 = new Vec3(p1.x, 0, p1.y);
			p2 = new Vec3(p2.x, 0, p2.y);
			r = new Vec3(r.x, 0, r.y);
			s = new Vec3(s.x, 0, s.y);
		}

		Vec3 qminusp = p2.subtract(p1);
		double rcs = r.x * s.z - r.z * s.x;
		Vec3 rdivrcs = r.scale(1 / rcs);
		Vec3 sdivrcs = s.scale(1 / rcs);
		double t = qminusp.x * sdivrcs.z - qminusp.z * sdivrcs.x;
		double u = qminusp.x * rdivrcs.z - qminusp.z * rdivrcs.x;
		return new double[]{t, u};
	}
}
