package dev.rdh.createunlimited.mixin;

import com.simibubi.create.content.trains.graph.TrackEdge;

import dev.rdh.createunlimited.config.CUConfig;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(TrackEdge.class)
public class TrackEdgeMixin {
	@ModifyConstant(method = "canTravelTo", constant = @Constant(doubleValue = 0.875), remap = false)
	private double canTravelTo(double original) {
		return CUConfig.extendedDriving.get() ? CUConfig.extendedDrivingValue.get() : original;
	}
}
