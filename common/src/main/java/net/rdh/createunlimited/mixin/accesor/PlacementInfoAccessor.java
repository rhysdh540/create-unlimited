package net.rdh.createunlimited.mixin.accesor;

import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.track.TrackPlacement.PlacementInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlacementInfo.class)
public interface PlacementInfoAccessor {
    @Accessor(value = "curve", remap = false) BezierConnection getCurve();
    @Accessor(value = "curve", remap = false) void setCurve(BezierConnection curve);

    @Accessor(value = "valid", remap = false) boolean getValid();
    @Accessor(value = "valid", remap = false) void setValid(boolean valid);

    @Accessor(value = "end1Extent", remap = false) int getEnd1Extent();
    @Accessor(value = "end1Extent", remap = false) void setEnd1Extent(int end1Extent);

    @Accessor(value = "end2Extent", remap = false) int getEnd2Extent();
    @Accessor(value = "end2Extent", remap = false) void setEnd2Extent(int end2Extent);

    @Accessor(value = "message", remap = false) String getMessage();
    @Accessor(value = "message", remap = false) void setMessage(String message);

    @Accessor(value = "end1", remap = false) Vec3 getEnd1();
    @Accessor(value = "end1", remap = false) void setEnd1(Vec3 end1);

    @Accessor(value = "end2", remap = false) Vec3 getEnd2();
    @Accessor(value = "end2", remap = false) void setEnd2(Vec3 end2);

    @Accessor(value = "normal1", remap = false) Vec3 getNormal1();
    @Accessor(value = "normal1", remap = false) void setNormal1(Vec3 normal1);

    @Accessor(value = "normal2", remap = false) Vec3 getNormal2();
    @Accessor(value = "normal2", remap = false) void setNormal2(Vec3 normal2);

    @Accessor(value = "axis1", remap = false) Vec3 getAxis1();
    @Accessor(value = "axis1", remap = false) void setAxis1(Vec3 axis1);

    @Accessor(value = "axis2", remap = false) Vec3 getAxis2();
    @Accessor(value = "axis2", remap = false) void setAxis2(Vec3 axis2);

    @Accessor(value = "pos1", remap = false) BlockPos getPos1();
    @Accessor(value = "pos1", remap = false) void setPos1(BlockPos pos1);

    @Accessor(value = "pos2", remap = false) BlockPos getPos2();
    @Accessor(value = "pos2", remap = false) void setPos2(BlockPos pos2);
}
