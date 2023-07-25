package net.examplemod.forge;

#if POST_MC_1_16_5
import dev.architectury.platform.forge.EventBuses;
#else
import me.shedaniel.architectury.platform.forge.EventBuses;
#endif
import net.examplemod.ExampleMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ExampleMod.MOD_ID)
public class ExampleModForge {
    public ExampleModForge() {
        PreLaunchSetup.onPreLaunch();

        EventBuses.registerModEventBus(ExampleMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        ExampleMod.init();
    }
}
