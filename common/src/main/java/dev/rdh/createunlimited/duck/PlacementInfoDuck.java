package dev.rdh.createunlimited.duck;

import dev.rdh.createunlimited.mixin.accessor.PlacementInfoAccessor;

import com.simibubi.create.content.trains.track.TrackPlacement.PlacementInfo;

public interface PlacementInfoDuck extends PlacementInfoAccessor {
	PlacementInfo self();
	PlacementInfo withMessage(String message);
}
