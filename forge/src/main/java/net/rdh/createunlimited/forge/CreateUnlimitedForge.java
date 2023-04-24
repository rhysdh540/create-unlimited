package net.rdh.createunlimited.forge;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.rdh.createunlimited.Config;
import net.rdh.createunlimited.CreateUnlimited;
import net.minecraftforge.fml.common.Mod;

@Mod(CreateUnlimited.MOD_ID)
public class CreateUnlimitedForge {
    public CreateUnlimitedForge() {
        CreateUnlimited.init();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        Config.loadConfig(Config.SPEC, FMLPaths.CONFIGDIR.get().resolve("createunlimited.toml"));
    }
}
