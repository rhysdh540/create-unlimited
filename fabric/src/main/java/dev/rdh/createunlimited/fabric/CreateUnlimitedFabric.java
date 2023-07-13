package dev.rdh.createunlimited.fabric;

import com.simibubi.create.foundation.config.ui.BaseConfigScreen;

import dev.rdh.createunlimited.CreateUnlimited;
import dev.rdh.createunlimited.config.CUConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class CreateUnlimitedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CreateUnlimited.init();
    }
}
