package dev.rdh.createunlimited.mixin;


import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionPacket;

import dev.rdh.createunlimited.config.CUConfig;

import net.minecraft.core.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(SuperGlueSelectionPacket.class)
public class SuperGlueSelectionPacketMixin {
	@ModifyConstant(method = "lambda$handle$0", constant = @Constant(doubleValue = 25), remap = false)
	private double modifyMaxSuperGlueDistance(double original) {
		return CUConfig.maxGlueConnectionRange.get();
	}
	@Redirect(method = "lambda$handle$0", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z", remap = false), remap = false)
	private boolean modifyNeedsConnected(Set<BlockPos> instance, Object o) {
		return instance.contains((BlockPos) o) || !CUConfig.physicalBlockConnection.get();
	}
}
