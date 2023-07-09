package dev.rdh.createunlimited.mixin;


import com.simibubi.create.infrastructure.config.CTrains;

import dev.rdh.createunlimited.CreateUnlimited;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CTrains.class)
public class CTrainsMixin {
	@ModifyArg(method = "<init>()V", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/infrastructure/config/CTrains;i(IIILjava/lang/String;[Ljava/lang/String;)Lcom/simibubi/create/foundation/config/ConfigBase$ConfigInt;", remap = false), index = 2, remap = false)
	private int modifyMaxTrackPlacementLength(int par1) {
		CreateUnlimited.LOGGER.info("CTrains config override loaded (probably)");
		return Integer.MAX_VALUE;
	}
}
