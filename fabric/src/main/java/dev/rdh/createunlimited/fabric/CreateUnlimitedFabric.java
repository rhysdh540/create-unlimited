package dev.rdh.createunlimited.fabric;

import dev.rdh.createunlimited.config.CUConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import dev.rdh.createunlimited.CreateUnlimited;
import net.fabricmc.api.ModInitializer;

public class CreateUnlimitedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CreateUnlimited.init();
        ModLoadingContext.registerConfig(CreateUnlimited.MOD_ID, ModConfig.Type.SERVER,  CUConfig.SPEC);
        CUConfig.init(CUConfig.SPEC, FabricLoader.getInstance().getConfigDir().resolve("createunlimited.toml"));
    }
}
