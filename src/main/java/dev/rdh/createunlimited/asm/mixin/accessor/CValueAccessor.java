package dev.rdh.createunlimited.asm.mixin.accessor;

import net.createmod.catnip.config.ConfigBase.CValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

#if MC >= 21
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
#else
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
#endif

@Mixin(value = CValue.class, remap = false)
public interface CValueAccessor {
	@Accessor("value")
	ConfigValue<?> getValue();
}
