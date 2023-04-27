package net.rdh.createunlimited.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.rdh.createunlimited.CreateUnlimited;
import net.fabricmc.api.ModInitializer;
import net.rdh.createunlimited.config.CUConfig;

public class CreateUnlimitedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CreateUnlimited.init();
        ModLoadingContext.registerConfig(CreateUnlimited.MOD_ID, ModConfig.Type.COMMON,  CUConfig.SPEC);
        CUConfig.init(CUConfig.SPEC, FabricLoader.getInstance().getConfigDir().resolve("createunlimited.toml"));
    }
}
