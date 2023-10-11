package dev.rdh.createunlimited.mixin.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackPlacement.PlacementInfo;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = PlacementInfo.class, remap = false)
@SuppressWarnings("unused")
public interface PlacementInfoAccessor {
	@Accessor("curve")
	BezierConnection getCurve();
	@Accessor("curve")
	void setCurve(BezierConnection curve);

	@Accessor("valid")
	boolean isValid();
	@Accessor("valid")
	void setValid(boolean valid);

	@Accessor("end1Extent")
	int getEnd1Extent();
	@Accessor("end1Extent")
	void setEnd1Extent(int end1Extent);

	@Accessor("end2Extent")
	int getEnd2Extent();
	@Accessor("end2Extent")
	void setEnd2Extent(int end2Extent);

	@Accessor("message")
	String getMessage();
	@Accessor("message")
	void setMessage(String message);

	//for visualization

	@Accessor("end1")
	Vec3 getEnd1();
	@Accessor("end1")
	void setEnd1(Vec3 end1);

	@Accessor("end2")
	Vec3 getEnd2();
	@Accessor("end2")
	void setEnd2(Vec3 end2);

	@Accessor("normal1")
	Vec3 getNormal1();
	@Accessor("normal1")
	void setNormal1(Vec3 normal1);

	@Accessor("normal2")
	Vec3 getNormal2();
	@Accessor("normal2")
	void setNormal2(Vec3 normal2);

	@Accessor("axis1")
	Vec3 getAxis1();
	@Accessor("axis1")
	void setAxis1(Vec3 axis1);

	@Accessor("axis2")
	Vec3 getAxis2();
	@Accessor("axis2")
	void setAxis2(Vec3 axis2);

	@Accessor("pos1")
	BlockPos getPos1();
	@Accessor("pos1")
	void setPos1(BlockPos pos1);

	@Accessor("pos2")
	BlockPos getPos2();
	@Accessor("pos2")
	void setPos2(BlockPos pos2);

	@Accessor("requiredTracks")
	int getRequiredTracks();
	@Accessor("requiredTracks")
	void setRequiredTracks(int requiredTracks);

	@Accessor("hasRequiredTracks")
	boolean hasRequiredTracks();
	@Accessor("hasRequiredTracks")
	void setHasRequiredTracks(boolean hasRequiredTracks);

	@Accessor("requiredPavement")
	int getRequiredPavement();
	@Accessor("requiredPavement")
	void setRequiredPavement(int requiredPavement);

	@Accessor("hasRequiredPavement")
	boolean hasRequiredPavement();
	@Accessor("hasRequiredPavement")
	void setHasRequiredPavement(boolean hasRequiredPavement);
}
