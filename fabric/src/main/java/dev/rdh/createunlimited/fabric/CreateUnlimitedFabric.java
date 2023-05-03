package dev.rdh.createunlimited.fabric;

import dev.rdh.createunlimited.CreateUnlimited;
import net.fabricmc.api.ModInitializer;

/**
 * The main class for the Fabric implementation of Create Unlimited.
 * It is called by Fabric when the game starts, and simply redirects to the common mod initializer.
 * @see dev.rdh.createunlimited.CreateUnlimited CreateUnlimited
 */
public class CreateUnlimitedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CreateUnlimited.init();
    }
}
