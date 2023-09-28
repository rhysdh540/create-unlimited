package dev.rdh.createunlimited.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.simibubi.create.content.trains.station.StationBlockEntity;
import dev.rdh.createunlimited.config.CUConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = StationBlockEntity.class, remap = false)
@SuppressWarnings("unused")
public abstract class StationBlockEntityMixin {
	@ModifyExpressionValue(method = "assemble", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/bogey/AbstractBogeyBlock;allowsSingleBogeyCarriage()Z", ordinal = 0))
	private boolean allowsSingleBogeyCarriage(boolean original) {
		return !CUConfigs.server().trainAssemblyChecks.get() || original;
	}

	@ModifyExpressionValue(method = "assemble", at = @At(value = "CONSTANT", args = "intValue=3", ordinal = 0))
	private int setMinBogeySpacing(int original) {
		return CUConfigs.server().trainAssemblyChecks.get() ? original : 0;
	}

	@Inject(method = "isValidBogeyOffset", at = @At("HEAD"), cancellable = true)
	private void disableBogeyOffsetCheck(CallbackInfoReturnable<Boolean> cir) {
		if(!CUConfigs.server().trainAssemblyChecks.get())
			cir.setReturnValue(true);
	}
}
