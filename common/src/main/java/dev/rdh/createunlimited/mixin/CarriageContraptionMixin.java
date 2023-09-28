package dev.rdh.createunlimited.mixin;

import com.simibubi.create.content.trains.entity.CarriageContraption;
import dev.rdh.createunlimited.config.CUConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(CarriageContraption.class)
public abstract class CarriageContraptionMixin {
	@Redirect(method = "assemble", at = @At(value = "INVOKE", target = "Ljava/util/Map;size()I", ordinal = 0))
	private int size(Map<?, ?> instance) {
		if(CUConfigs.server().trainAssemblyChecks.get())
			return instance.size();
		else
			return 2;
	}
}
