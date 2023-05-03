package dev.rdh.createunlimited.fabric;

import dev.rdh.createunlimited.CreateUnlimited;
import dev.rdh.createunlimited.config.CUConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

/**
 * The main class for the Fabric implementation of Create Unlimited.
 * It is called by Fabric when the game starts, and simply redirects to the common mod initializer.
 * @see dev.rdh.createunlimited.CreateUnlimited CreateUnlimited
 */
public class CreateUnlimitedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ModLoadingContext.registerConfig(CreateUnlimited.MOD_ID, ModConfig.Type.SERVER, CUConfig.SPEC, "createunlimited.toml");
        CreateUnlimited.init();
    }
}
