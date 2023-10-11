package dev.rdh.createunlimited.mixin.accessor;

import manifold.rt.api.NoBootstrap;

import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

import com.simibubi.create.foundation.config.ConfigBase.CValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@NoBootstrap
@Mixin(value = CValue.class, remap = false)
public interface CValueAccessor<V, T extends ConfigValue<V>> {
	@Accessor("value")
	ConfigValue<V> getValue();
}
