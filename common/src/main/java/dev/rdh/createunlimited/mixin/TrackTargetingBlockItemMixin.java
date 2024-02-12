package dev.rdh.createunlimited.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import dev.rdh.createunlimited.Util;
import dev.rdh.createunlimited.config.CUConfigs;

import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;

@Mixin(value = TrackTargetingBlockItem.class, remap = false)
public class TrackTargetingBlockItemMixin {
	@ModifyExpressionValue(method = "useOn", at = {
		@At(value = "CONSTANT", args = "doubleValue=80.0"),
		@At(value = "CONSTANT", args = "doubleValue=16.0"),
	})
	private double modifyMaxDistance(double original) {
		return Util.orElse(CUConfigs.server.maxTrackBlockPlacingDistance, original);
	}
}
