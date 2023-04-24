package net.rdh.createunlimited.fabric;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
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
    }
}
