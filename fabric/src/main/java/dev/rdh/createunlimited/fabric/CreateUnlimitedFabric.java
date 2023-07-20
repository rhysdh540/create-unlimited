package dev.rdh.createunlimited.fabric;

import dev.rdh.createunlimited.CreateUnlimited;
import net.fabricmc.api.ModInitializer;

public class CreateUnlimitedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CreateUnlimited.init();
    }
}
