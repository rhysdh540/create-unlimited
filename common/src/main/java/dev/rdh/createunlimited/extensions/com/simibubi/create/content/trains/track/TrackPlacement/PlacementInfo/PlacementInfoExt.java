package dev.rdh.createunlimited.extensions.com.simibubi.create.content.trains.track.TrackPlacement.PlacementInfo;

import dev.rdh.createunlimited.mixin.accessor.PlacementInfoAccessor;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackPlacement.PlacementInfo;

@Extension
@SuppressWarnings("unused")
public class PlacementInfoExt {
	public static BezierConnection getCurve(@This PlacementInfo info) {
		return ((PlacementInfoAccessor) info).getCurve();
	}
	public static void setCurve(@This PlacementInfo info, BezierConnection curve) {
		((PlacementInfoAccessor) info).setCurve(curve);
	}

	public static boolean isValid(@This PlacementInfo info) {
		return ((PlacementInfoAccessor) info).isValid();
	}
	public static void setValid(@This PlacementInfo info, boolean valid) {
		((PlacementInfoAccessor) info).setValid(valid);
	}

	public static int getEnd1Extent(@This PlacementInfo info) {
		return ((PlacementInfoAccessor) info).getEnd1Extent();
	}
	public static void setEnd1Extent(@This PlacementInfo info, int end1Extent) {
		((PlacementInfoAccessor) info).setEnd1Extent(end1Extent);
	}

	public static int getEnd2Extent(@This PlacementInfo info) {
		return ((PlacementInfoAccessor) info).getEnd2Extent();
	}
	public static void setEnd2Extent(@This PlacementInfo info, int end2Extent) {
		((PlacementInfoAccessor) info).setEnd2Extent(end2Extent);
	}

	public static String getMessage(@This PlacementInfo info) {
		return ((PlacementInfoAccessor) info).getMessage();
	}
	public static void setMessage(@This PlacementInfo info, String message) {
		((PlacementInfoAccessor) info).setMessage(message);
	}

	public static Vec3 getEnd1(@This PlacementInfo info) {
		return ((PlacementInfoAccessor) info).getEnd1();
	}
	public static void setEnd1(@This PlacementInfo info, Vec3 end1) {
		((PlacementInfoAccessor) info).setEnd1(end1);
	}

	public static Vec3 getEnd2(@This PlacementInfo info) {
		return ((PlacementInfoAccessor) info).getEnd2();
	}
	public static void setEnd2(@This PlacementInfo info, Vec3 end2) {
		((PlacementInfoAccessor) info).setEnd2(end2);
	}

	public static Vec3 getNormal1(@This PlacementInfo info) {
		return ((PlacementInfoAccessor) info).getNormal1();
	}
	public static void setNormal1(@This PlacementInfo info, Vec3 normal1) {
		((PlacementInfoAccessor) info).setNormal1(normal1);
	}

	public static Vec3 getNormal2(@This PlacementInfo info) {
		return ((PlacementInfoAccessor) info).getNormal2();
	}
	public static void setNormal2(@This PlacementInfo info, Vec3 normal2) {
		((PlacementInfoAccessor) info).setNormal2(normal2);
	}

	public static Vec3 getAxis1(@This PlacementInfo info) {
		return ((PlacementInfoAccessor) info).getAxis1();
	}
	public static void setAxis1(@This PlacementInfo info, Vec3 axis1) {
		((PlacementInfoAccessor) info).setAxis1(axis1);
	}

	public static Vec3 getAxis2(@This PlacementInfo info) {
		return ((PlacementInfoAccessor) info).getAxis2();
	}
	public static void setAxis2(@This PlacementInfo info, Vec3 axis2) {
		((PlacementInfoAccessor) info).setAxis2(axis2);
	}

	public static BlockPos getPos1(@This PlacementInfo info) {
		return ((PlacementInfoAccessor) info).getPos1();
	}
	public static void setPos1(@This PlacementInfo info, BlockPos pos1) {
		((PlacementInfoAccessor) info).setPos1(pos1);
	}

	public static BlockPos getPos2(@This PlacementInfo info) {
		return ((PlacementInfoAccessor) info).getPos2();
	}
	public static void setPos2(@This PlacementInfo info, BlockPos pos2) {
		((PlacementInfoAccessor) info).setPos2(pos2);
	}
}