package dev.rdh.createunlimited.fabric;

import dev.rdh.createunlimited.CreateUnlimited;

import dev.rdh.createunlimited.config.CUConfigs;

#if PRE_CURRENT_MC_1_19_2
import net.minecraftforge.api.fml.event.config.ModConfigEvents;
#elif POST_CURRENT_MC_1_20_1
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
#endif import manifold.rt.api.NoBootstrap;
import net.fabricmc.api.ModInitializer;

@NoBootstrap
public class CreateUnlimitedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
		ModConfigEvents.loading(CreateUnlimited.ID).register(CUConfigs::onLoad);
		ModConfigEvents.reloading(CreateUnlimited.ID).register(CUConfigs::onReload);
        CreateUnlimited.init();
    }
}
