package net.rdh.createunlimited.fabric;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.rdh.createunlimited.Config;
import net.rdh.createunlimited.CreateUnlimited;
import net.fabricmc.api.ModInitializer;

public class CreateUnlimitedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CreateUnlimited.init();
        CreateUnlimited.LOGGER.info(EnvExecutor.unsafeRunForDist(
                () -> () -> "{} is accessing Porting Lib on a Fabric client!",
                () -> () -> "{} is accessing Porting Lib on a Fabric server!"
                ), CreateUnlimited.NAME);
        ModLoadingContext.registerConfig(CreateUnlimited.MOD_ID, ModConfig.Type.COMMON,  Config.SPEC);
        Config.loadConfig(Config.SPEC, FabricLoader.getInstance().getConfigDir().resolve("createunlimited.toml"));
    }
}
