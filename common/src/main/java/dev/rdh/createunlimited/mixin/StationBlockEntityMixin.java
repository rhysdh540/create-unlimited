package dev.rdh.createunlimited.mixin;

import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import dev.rdh.createunlimited.config.CUConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = StationBlockEntity.class, remap = false)
public class StationBlockEntityMixin {
	@Redirect(method = "assemble", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/bogey/AbstractBogeyBlock;allowsSingleBogeyCarriage()Z", ordinal = 0))
	private boolean allowsSingleBogeyCarriage(AbstractBogeyBlock<?> instance) {
		if(CUConfigs.server().trainAssemblyChecks.get())
			return instance.allowsSingleBogeyCarriage();
		else
			return true;
	}

	@ModifyConstant(method = "assemble", constant = @Constant(intValue = 3, ordinal = 0))
	private int setMinBogeySpacing(int original) {
		if(CUConfigs.server().trainAssemblyChecks.get())
			return original;
		else
			return 0;
	}

	@Inject(method = "isValidBogeyOffset", at = @At("HEAD"), cancellable = true)
	private void disableBogeyOffsetCheck(CallbackInfoReturnable<Boolean> cir) {
		if(!CUConfigs.server().trainAssemblyChecks.get())
			cir.setReturnValue(true);
	}
}
