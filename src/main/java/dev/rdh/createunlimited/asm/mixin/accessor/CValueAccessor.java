package dev.rdh.createunlimited.asm.mixin.accessor;

import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

import net.createmod.catnip.config.ConfigBase.CValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = CValue.class, remap = false)
public interface CValueAccessor {
	@Accessor("value")
	ConfigValue<?> getValue();
}
