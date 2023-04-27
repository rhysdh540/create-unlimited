package net.rdh.createunlimited.forge;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.rdh.createunlimited.CreateUnlimited;
import net.minecraftforge.fml.common.Mod;
import net.rdh.createunlimited.config.CUConfig;

@Mod(CreateUnlimited.MOD_ID)
public class CreateUnlimitedForge {
    public CreateUnlimitedForge() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CUConfig.SPEC);
        CUConfig.init(CUConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve("createunlimited.toml"));
        CreateUnlimited.init();
    }
}
