package dev.rdh.createunlimited.mixin;


import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionHandler;

import dev.rdh.createunlimited.config.CUConfig;

import net.minecraft.core.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(SuperGlueSelectionHandler.class)
public class SuperGlueSectionHandlerMixin {
	@ModifyConstant(method = "tick", constant = @Constant(doubleValue = 24), remap = false)
	private double modifyMaxSuperGlueDistance(double original) {
		return CUConfig.maxGlueConnectionRange.get();
	}
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"), remap = false)
	private boolean modifyNeedsConnected(Set<BlockPos> instance, Object o) {
		return instance.contains((BlockPos) o) || !CUConfig.physicalBlockConnection.get();
	}
	@Redirect(method = "onMouseInput", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"), remap = false)
	private boolean modifyNeedsConnectedClick(Set<BlockPos> instance, Object o) {
		return instance.contains((BlockPos) o) || !CUConfig.physicalBlockConnection.get();
	}
}
