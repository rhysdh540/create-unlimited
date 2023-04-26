package net.rdh.createunlimited.mixin.accessor;

import com.simibubi.create.foundation.config.ConfigBase;
import net.minecraftforge.common.ForgeConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ConfigBase.class)
public interface ConfigBaseAccessor {
    @Invoker(value = "registerAll", remap = false) void callRegisterAll(final ForgeConfigSpec.Builder builder);
}
