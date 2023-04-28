package dev.rdh.createunlimited.forge;

import dev.rdh.createunlimited.config.CUConfig;
import dev.rdh.createunlimited.config.CUConfigScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import dev.rdh.createunlimited.CreateUnlimited;
import net.minecraftforge.fml.common.Mod;

@Mod(CreateUnlimited.MOD_ID)
@SuppressWarnings("UnstableApiUsage")
public class CreateUnlimitedForge {
    public CreateUnlimitedForge() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CUConfig.SPEC);
        CUConfig.init(CUConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve("createunlimited.toml"));
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((minecraftClient, screen) -> new CUConfigScreen(screen)));
                // double lambda o.O
        CreateUnlimited.init();
    }
}
