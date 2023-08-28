package dev.rdh.createunlimited.fabric;

import dev.rdh.createunlimited.CreateUnlimited;

import dev.rdh.createunlimited.config.CUConfigs;

import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import net.fabricmc.api.ModInitializer;

public class CreateUnlimitedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
		ModConfigEvents.loading(CreateUnlimited.ID).register(CUConfigs::onLoad);
		ModConfigEvents.reloading(CreateUnlimited.ID).register(CUConfigs::onReload);
        CreateUnlimited.init();
    }
}
