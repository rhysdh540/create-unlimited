package dev.rdh.createunlimited.mixin.accessor;

import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

import com.simibubi.create.foundation.config.ConfigBase.CValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = CValue.class, remap = false)
public interface CValueAccessor {
	@Accessor("value")
	ConfigValue<?> getValue();
}
