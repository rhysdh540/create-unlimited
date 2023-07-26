package dev.rdh.createunlimited.fabric;

import dev.rdh.createunlimited.ExampleMod;

import net.fabricmc.api.ModInitializer;

public class ExampleModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ExampleMod.init();
    }
}
