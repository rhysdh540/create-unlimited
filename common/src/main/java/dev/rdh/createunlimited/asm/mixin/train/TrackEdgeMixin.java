package dev.rdh.createunlimited.asm.mixin.train;

import com.simibubi.create.content.trains.graph.TrackEdge;

import dev.rdh.createunlimited.Util;
import dev.rdh.createunlimited.config.CUConfigs;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TrackEdge.class, remap = false)
public abstract class TrackEdgeMixin {
	@ModifyExpressionValue(method = "canTravelTo", at = @At(value = "CONSTANT", args = "doubleValue=0.875"))
	private double canTravelTo(double original) {
		return Util.orElse(CUConfigs.server.extendedDriving, original);
	}
}
