package dev.rdh.createunlimited.asm.mixin.train;

import com.simibubi.create.content.trains.station.StationBlockEntity;

import dev.rdh.createunlimited.config.CUConfig;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(value = StationBlockEntity.class, remap = false)
public abstract class StationBlockEntityMixin {
	@ModifyExpressionValue(method = "assemble", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/bogey/AbstractBogeyBlock;allowsSingleBogeyCarriage()Z", ordinal = 0))
	private boolean forceAllowSingleBogeyCarriage(boolean original) {
		return original || !CUConfig.getOrTrue(CUConfig.instance.trainAssemblyChecks);
	}

	@ModifyExpressionValue(method = "assemble", at = @At(value = "CONSTANT", args = "intValue=3", ordinal = 0))
	private int setMinBogeySpacing(int original) {
		return CUConfig.instance.trainAssemblyChecks.get() ? original : 0;
	}

	@Inject(method = "isValidBogeyOffset", at = @At("HEAD"), cancellable = true)
	private void disableBogeyOffsetCheck(CallbackInfoReturnable<Boolean> cir) {
		if(!CUConfig.getOrTrue(CUConfig.instance.trainAssemblyChecks))
			cir.setReturnValue(true);
	}
}
