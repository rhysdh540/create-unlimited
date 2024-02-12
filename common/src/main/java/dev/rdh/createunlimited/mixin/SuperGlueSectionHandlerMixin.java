package dev.rdh.createunlimited.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionHandler;

import dev.rdh.createunlimited.Util;
import dev.rdh.createunlimited.config.CUConfigs;

@Mixin(value = SuperGlueSelectionHandler.class, remap = false)
public abstract class SuperGlueSectionHandlerMixin {
	// client-side modification
	@ModifyConstant(method = "tick", constant = @Constant(doubleValue = 24))
	private double modifyMaxSuperGlueDistance(double original) {
		return Util.orElse(CUConfigs.server.maxGlueConnectionRange, original);
	}
}
