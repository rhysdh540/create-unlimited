package dev.rdh.createunlimited.mixin;

import dev.rdh.createunlimited.duck.PlacementInfoDuck;

import com.simibubi.create.content.trains.track.TrackPlacement.PlacementInfo;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings({ "DataFlowIssue", "AddedMixinMembersNamePattern" })
@Mixin(value = PlacementInfo.class, remap = false)
public abstract class PlacementInfoMixin implements PlacementInfoDuck {
	@Unique(silent = true) // at runtime just use the original method
	@Override
	public PlacementInfo withMessage(String message) {
		return ((PlacementInfo) (Object) this).withMessage(message);
	}

	@Unique
	public PlacementInfo self() {
		return (PlacementInfo) (Object) this;
	}
}
