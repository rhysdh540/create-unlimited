package dev.rdh.createunlimited.mixin;

import com.simibubi.create.content.trains.graph.TrackEdge;

import dev.rdh.createunlimited.Util;
import dev.rdh.createunlimited.config.CUConfigs;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(value = TrackEdge.class, remap = false)
public abstract class TrackEdgeMixin {
	@ModifyConstant(method = "canTravelTo", constant = @Constant(doubleValue = 0.875))
	private double canTravelTo(double original) {
		return Util.orElse(CUConfigs.server.extendedDriving, original);
	}
}
