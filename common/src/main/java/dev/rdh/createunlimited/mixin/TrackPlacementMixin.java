package dev.rdh.createunlimited.mixin;

import com.simibubi.create.content.trains.track.TrackPlacement;

import org.spongepowered.asm.mixin.Mixin;

// all the instrumenting for this class is done in the mixin config plugin
@Mixin(value = TrackPlacement.class, remap = false, priority = 0)
public abstract class TrackPlacementMixin {

}
