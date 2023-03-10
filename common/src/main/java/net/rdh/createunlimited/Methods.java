package net.rdh.createunlimited;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.ITrackBlock;
import com.simibubi.create.content.logistics.trains.track.TrackBlock;
import com.simibubi.create.content.logistics.trains.track.TrackPlacement;
import com.simibubi.create.content.logistics.trains.track.TrackPlacement.PlacementInfo;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.utility.*;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemHandlerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.rdh.createunlimited.mixin.accessors.PlacementInfoAccessor;
import net.rdh.createunlimited.mixin.accessors.TrackPlacementAccessor;

import static com.simibubi.create.content.logistics.trains.track.TrackPlacement.cached;

public class Methods {
    public static final String tryConnectRef ="Lcom/simibubi/create/content/logistics/trains/track/TrackPlacement;tryConnect(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/item/ItemStack;ZZ)Lcom/simibubi/create/content/logistics/trains/track/TrackPlacement$PlacementInfo;";

    @SuppressWarnings({"Duplicates", "ConstantValue"})
    public static PlacementInfo tryConnectLoose(Level level, Player player, BlockPos pos2, BlockState state2, ItemStack stack, boolean girder, boolean maximiseTurn) {
        if(!Config.PLACEMENT_ENABLED.get())
            return TrackPlacement.tryConnect(level, player, pos2, state2, stack, girder, maximiseTurn);

        // CreateUnlimited.LOGGER.info("Track Placement Method Called!");
        Vec3 lookVec = player.getLookAngle();
        int lookAngle = (int) (22.5 + AngleHelper.deg(Mth.atan2(lookVec.z, lookVec.x)) % 360) / 8;
        int maxLength = AllConfigs.SERVER.trains.maxTrackPlacementLength.get();

        if (level.isClientSide && cached != null && pos2.equals(TrackPlacementAccessor.getHoveringPos()) && stack.equals(TrackPlacementAccessor.getLastItem())
                && TrackPlacementAccessor.getHoveringMaxed() == maximiseTurn && lookAngle == TrackPlacementAccessor.getHoveringAngle())
            return cached;

        PlacementInfo info = new PlacementInfo();
        TrackPlacementAccessor.setHoveringMaxed(maximiseTurn);
        TrackPlacementAccessor.setHoveringAngle(lookAngle);
        TrackPlacementAccessor.setHoveringPos(pos2);
        TrackPlacementAccessor.setLastItem(stack);
        cached = info;
        ((PlacementInfoAccessor)info).setValid(false);

        ITrackBlock track = (ITrackBlock) state2.getBlock();
        Pair<Vec3, AxisDirection> nearestTrackAxis = track.getNearestTrackAxis(level, pos2, state2, lookVec);
        Vec3 axis2 = nearestTrackAxis.getFirst()
                .scale(nearestTrackAxis.getSecond() == Direction.AxisDirection.POSITIVE ? -1 : 1);
        Vec3 normal2 = track.getUpNormal(level, pos2, state2)
                .normalize();
        Vec3 normedAxis2 = axis2.normalize();
        Vec3 end2 = track.getCurveStart(level, pos2, state2, axis2);

        CompoundTag itemTag = stack.getTag();
        CompoundTag selectionTag = itemTag.getCompound("ConnectingFrom");
        BlockPos pos1 = NbtUtils.readBlockPos(selectionTag.getCompound("Pos"));
        Vec3 axis1 = VecHelper.readNBT(selectionTag.getList("Axis", Tag.TAG_DOUBLE));
        Vec3 normedAxis1 = axis1.normalize();
        Vec3 end1 = VecHelper.readNBT(selectionTag.getList("End", Tag.TAG_DOUBLE));
        Vec3 normal1 = VecHelper.readNBT(selectionTag.getList("Normal", Tag.TAG_DOUBLE));
        BlockState state1 = level.getBlockState(pos1);

        if (level.isClientSide) {
            ((PlacementInfoAccessor)info).setEnd1(end1);
            ((PlacementInfoAccessor)info).setEnd2(end2);
            ((PlacementInfoAccessor)info).setAxis1(axis1);
            ((PlacementInfoAccessor)info).setAxis2(axis2);
            ((PlacementInfoAccessor)info).setNormal1(normal1);
            ((PlacementInfoAccessor)info).setNormal2(normal2);
        }

        if (pos1.equals(pos2))
            return info.withMessage("second_point");
        if (pos1.distSqr(pos2) > maxLength * maxLength)
            return info.withMessage("too_far")
                    .tooJumbly();
        if (!state1.hasProperty(TrackBlock.HAS_TE))
            return info.withMessage("original_missing");

        if (axis1.dot(end2.subtract(end1)) < 0) {
            axis1 = axis1.scale(-1);
            normedAxis1 = normedAxis1.scale(-1);
            end1 = track.getCurveStart(level, pos1, state1, axis1);
            if (level.isClientSide) {
                ((PlacementInfoAccessor)info).setEnd1(end1);
                ((PlacementInfoAccessor)info).setAxis1(axis1);
            }
        }

        double[] intersect = VecHelper.intersect(end1, end2, normedAxis1, normedAxis2, Direction.Axis.Y);
        boolean parallel = intersect == null;
        boolean skipCurve = false;

        if ((parallel && normedAxis1.dot(normedAxis2) > 0) || (!parallel && (intersect[0] < 0 || intersect[1] < 0))) {
            axis2 = axis2.scale(-1);
            normedAxis2 = normedAxis2.scale(-1);
            end2 = track.getCurveStart(level, pos2, state2, axis2);
            if (level.isClientSide) {
                ((PlacementInfoAccessor)info).setEnd2(end2);
                ((PlacementInfoAccessor)info).setAxis2(axis2);
            }
        }

        Vec3 cross2 = normedAxis2.cross(new Vec3(0, 1, 0));

        double a1 = Mth.atan2(normedAxis2.z, normedAxis2.x);
        double a2 = Mth.atan2(normedAxis1.z, normedAxis1.x);
        double angle = a1 - a2;
        double ascend = end2.subtract(end1).y;
        double absAscend = Math.abs(ascend);
        boolean slope = !normal1.equals(normal2);

        if (level.isClientSide) {
            Vec3 offset1 = axis1.scale(((PlacementInfoAccessor)info).getEnd1Extent());
            Vec3 offset2 = axis2.scale(((PlacementInfoAccessor)info).getEnd2Extent());
            BlockPos targetPos1 = pos1.offset(offset1.x, offset1.y, offset1.z);
            BlockPos targetPos2 = pos2.offset(offset2.x, offset2.y, offset2.z);
            ((PlacementInfoAccessor)info).setCurve(new BezierConnection(Couple.create(targetPos1, targetPos2),
                    Couple.create(end1.add(offset1), end2.add(offset2)), Couple.create(normedAxis1, normedAxis2),
                    Couple.create(normal1, normal2), true, girder));
        }

        // S curve or Straight

        double dist = 0;

        if (parallel) {
            double[] sTest = VecHelper.intersect(end1, end2, normedAxis1, cross2, Direction.Axis.Y);
            if (sTest != null) {
                double t = Math.abs(sTest[0]);
                double u = Math.abs(sTest[1]);

                skipCurve = Mth.equal(u, 0);
                if (skipCurve) {
                    dist = VecHelper.getCenterOf(pos1)
                            .distanceTo(VecHelper.getCenterOf(pos2));
                    ((PlacementInfoAccessor)info).setEnd1Extent((int) Math.round((dist + 1) / axis1.length()));

                } else {
                    double targetT = u <= 1 ? 3 : u * 2;

                    // This is for standardising s curve sizes
                    if (t > targetT) {
                        int correction = (int) ((t - targetT) / axis1.length());
                        ((PlacementInfoAccessor)info).setEnd1Extent(maximiseTurn ? 0 : correction / 2 + (correction % 2));
                        ((PlacementInfoAccessor)info).setEnd2Extent(maximiseTurn ? 0 : correction / 2);
                    }
                }
            }
        }

        // Slope

        if (slope) {
            skipCurve = false;
            ((PlacementInfoAccessor)info).setEnd1Extent(0);
            ((PlacementInfoAccessor)info).setEnd2Extent(0);

            Direction.Axis plane = Mth.equal(axis1.x, 0) ? Direction.Axis.X : Direction.Axis.Z;
            intersect = VecHelper.intersect(end1, end2, normedAxis1, normedAxis2, plane);
            if(intersect == null)
                intersect = intersectNoNull(end1, end2, normedAxis1, normedAxis2, plane);
            double dist1 = Math.abs(intersect[0] / axis1.length());
            double dist2 = Math.abs(intersect[1] / axis2.length());

            if (dist1 > dist2)
                ((PlacementInfoAccessor)info).setEnd1Extent((int) Math.round(dist1 - dist2));
            if (dist2 > dist1)
                ((PlacementInfoAccessor)info).setEnd2Extent((int) Math.round(dist2 - dist1));

            double turnSize = Math.min(dist1, dist2);
            // This is for standardising curve sizes
            if (turnSize > 2 && !maximiseTurn) {
                ((PlacementInfoAccessor)info).setEnd1Extent(((PlacementInfoAccessor)info).getEnd1Extent()-2);
                ((PlacementInfoAccessor)info).setEnd2Extent(((PlacementInfoAccessor)info).getEnd2Extent()-2);
            }
        }

        // Straight ascend

        if (skipCurve && !Mth.equal(ascend, 0)) {
            int hDistance = ((PlacementInfoAccessor)info).getEnd1Extent();
            if (axis1.y == 0 || !Mth.equal(absAscend + 1, dist / axis1.length())) {
                ((PlacementInfoAccessor)info).setEnd1Extent(0);

                double minHDistance = Math.max(absAscend < 4 ? absAscend * 4 : absAscend * 3, 6) / axis1.length();
                if (hDistance > minHDistance) {
                    int correction = (int) (hDistance - minHDistance);
                    ((PlacementInfoAccessor)info).setEnd1Extent(maximiseTurn ? 0 : correction / 2 + (correction % 2));
                    ((PlacementInfoAccessor)info).setEnd2Extent(maximiseTurn ? 0 : correction / 2);
                }

                skipCurve = false;
            }
        }

        // Turn

        if (!parallel) {
            float absAngle = Math.abs(AngleHelper.deg(angle));
            intersect = VecHelper.intersect(end1, end2, normedAxis1, normedAxis2, Direction.Axis.Y);
            if(intersect == null)
                intersect = intersectNoNull(end1, end2, normedAxis1, normedAxis2, Direction.Axis.Y);
            double dist1 = Math.abs(intersect[0]);
            double dist2 = Math.abs(intersect[1]);
            float ex1 = 0;
            float ex2 = 0;

            if (dist1 > dist2)
                ex1 = (float) ((dist1 - dist2) / axis1.length());
            if (dist2 > dist1)
                ex2 = (float) ((dist2 - dist1) / axis2.length());

            double turnSize = Math.min(dist1, dist2) - .1d;
            boolean ninety = (absAngle + .25f) % 90 < 1;
            double minTurnSize = ninety ? 7 : 3.25;
            double turnSizeToFitAscend =
                    minTurnSize + (ninety ? Math.max(0, absAscend - 3) * 2f : Math.max(0, absAscend - 1.5f) * 1.5f);

            // This is for standardising curve sizes
            if (!maximiseTurn) {
                ex1 += (turnSize - turnSizeToFitAscend) / axis1.length();
                ex2 += (turnSize - turnSizeToFitAscend) / axis2.length();
            }
            ((PlacementInfoAccessor)info).setEnd1Extent(Mth.floor(ex1));
            ((PlacementInfoAccessor)info).setEnd2Extent(Mth.floor(ex2));
        }

        Vec3 offset1 = axis1.scale(((PlacementInfoAccessor)info).getEnd1Extent());
        Vec3 offset2 = axis2.scale(((PlacementInfoAccessor)info).getEnd2Extent());
        BlockPos targetPos1 = pos1.offset(offset1.x, offset1.y, offset1.z);
        BlockPos targetPos2 = pos2.offset(offset2.x, offset2.y, offset2.z);

        ((PlacementInfoAccessor)info).setCurve(skipCurve ? null
                : new BezierConnection(Couple.create(targetPos1, targetPos2),
                Couple.create(end1.add(offset1), end2.add(offset2)), Couple.create(normedAxis1, normedAxis2),
                Couple.create(normal1, normal2), true, girder));

        ((PlacementInfoAccessor)info).setValid(true);
        ((PlacementInfoAccessor)info).setMessage(null);

        ((PlacementInfoAccessor)info).setPos1(pos1);
        ((PlacementInfoAccessor)info).setPos2(pos2);
        ((PlacementInfoAccessor)info).setAxis1(axis1);
        ((PlacementInfoAccessor)info).setAxis2(axis2);

        TrackPlacementAccessor.invokePlaceTracks(level, info, state1, state2, targetPos1, targetPos2, true);

        ItemStack offhandItem = player.getOffhandItem()
                .copy();
        boolean shouldPave = offhandItem.getItem() instanceof BlockItem;
        if (shouldPave) {
            BlockItem paveItem = (BlockItem) offhandItem.getItem();
            TrackPlacementAccessor.invokePaveTracks(level, info, paveItem, true);
            info.hasRequiredPavement = true;
        }

        info.hasRequiredTracks = true;

        if (!player.isCreative()) {
            for (boolean simulate : Iterate.trueAndFalse) {
                if (level.isClientSide && !simulate)
                    break;

                int tracks = info.requiredTracks;
                int pavement = info.requiredPavement;
                int foundTracks = 0;
                int foundPavement = 0;

                Inventory inv = player.getInventory();
                int size = inv.items.size();
                for (int j = 0; j <= size + 1; j++) {
                    int i = j;
                    boolean offhand = j == size + 1;
                    if (j == size)
                        i = inv.selected;
                    else if (offhand)
                        i = 0;
                    else if (j == inv.selected)
                        continue;

                    ItemStack stackInSlot = (offhand ? inv.offhand : inv.items).get(i);
                    boolean isTrack = AllBlocks.TRACK.isIn(stackInSlot);
                    if (!isTrack && (!shouldPave || offhandItem.getItem() != stackInSlot.getItem()))
                        continue;
                    if (isTrack ? foundTracks >= tracks : foundPavement >= pavement)
                        continue;

                    int count = stackInSlot.getCount();

                    if (!simulate) {
                        int remainingItems =
                                count - Math.min(isTrack ? tracks - foundTracks : pavement - foundPavement, count);
                        if (i == inv.selected)
                            stackInSlot.setTag(null);
                        ItemStack newItem = ItemHandlerHelper.copyStackWithSize(stackInSlot, remainingItems);
                        if (offhand)
                            player.setItemInHand(InteractionHand.OFF_HAND, newItem);
                        else
                            inv.setItem(i, newItem);
                    }

                    if (isTrack) foundTracks += count;
                    else foundPavement += count;
                }

                if (simulate && foundTracks < tracks) {
                    ((PlacementInfoAccessor)info).setValid(false);
                    info.tooJumbly();
                    info.hasRequiredTracks = false;
                    return info.withMessage("not_enough_tracks");
                }

                if (simulate && foundPavement < pavement) {
                    ((PlacementInfoAccessor)info).setValid(false);
                    info.tooJumbly();
                    info.hasRequiredPavement = false;
                    return info.withMessage("not_enough_pavement");
                }
            }
        }

        if (level.isClientSide())
            return info;
        if (shouldPave) {
            BlockItem paveItem = (BlockItem) offhandItem.getItem();
            TrackPlacementAccessor.invokePaveTracks(level, info, paveItem, false);
        }
        if (((PlacementInfoAccessor)info).getCurve() != null && ((PlacementInfoAccessor)info).getCurve().getLength() > 120)
            AllAdvancements.LONG_BEND.awardTo(player);

        return TrackPlacementAccessor.invokePlaceTracks(level, info, state1, state2, targetPos1, targetPos2, false);
    }
    public static double[] intersectNoNull(Vec3 p1, Vec3 p2, Vec3 r, Vec3 s, Axis plane) {
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
        if (Mth.equal(rcs, 0))
            return null;
        Vec3 rdivrcs = r.scale(1 / rcs);
        Vec3 sdivrcs = s.scale(1 / rcs);
        double t = qminusp.x * sdivrcs.z - qminusp.z * sdivrcs.x;
        double u = qminusp.x * rdivrcs.z - qminusp.z * rdivrcs.x;
        return new double[] { t, u };
    }
}