package net.examplemod;

#if POST_MC_1_16_5
import dev.architectury.platform.Platform;
#else
import me.shedaniel.architectury.platform.Platform;
#endif

public class ExampleMod {
    public static final String MOD_ID = "examplemod";
    
    public static void init() {        
        System.out.println(ExampleExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());

        #if POST_CURRENT_MC_1_16_5 && MC_1_19_2
        System.out.println("Is post 1.16.5 and is 1.19.2");
        #elif POST_CURRENT_MC_1_16_5
        System.out.println("Is 1.16.5 or post 1.16.5");
        #endif

        #if POST_MC_1_16_5
        System.out.println("Is post 1.16.5");
        #elif PRE_MC_1_18_2
        System.out.println("Is pre 1.18.2");
        #endif

        #if PRE_CURRENT_MC_1_18_2
        System.out.println("Is 1.18.2 or pre 1.18.2");
        #endif

        if (Platform.isFabric()) {
            System.out.println("Is Fabric");
        } else if (Platform.isForge()) {
            System.out.println("Is Forge");
        }
    }
}
