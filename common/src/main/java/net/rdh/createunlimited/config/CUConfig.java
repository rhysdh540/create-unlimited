package net.rdh.createunlimited.config;

import com.simibubi.create.foundation.config.ConfigBase;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.rdh.createunlimited.CreateUnlimited;
import net.rdh.createunlimited.mixin.accessor.ConfigBaseAccessor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class CUConfig {
    private static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);

    public static CUServerConfig SERVER;

    public static void init() {
        CreateUnlimited.LOGGER.info("Initializing config");
        SERVER = register(CUServerConfig::new, ModConfig.Type.SERVER);

        for (Map.Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet())
            ModLoadingContext.registerConfig(CreateUnlimited.MOD_ID, pair.getKey(), pair.getValue().specification);
    }

    public static <T extends ConfigBase> T register(Supplier<T> factory, ModConfig.Type side) {
        CreateUnlimited.LOGGER.info("Registering {} side config file", side.name().toLowerCase());
        Pair<T, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(builder -> {
            T configSide = factory.get();
            ((ConfigBaseAccessor)configSide).callRegisterAll(builder);
            return configSide;
        });
        T config = specPair.getLeft();
        config.specification = specPair.getRight();
        CONFIGS.put(side, config);
        return config;
    }
}
