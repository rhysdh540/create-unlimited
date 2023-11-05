package dev.rdh.createunlimited.config;

import com.simibubi.create.foundation.config.ConfigBase;
import com.simibubi.create.foundation.config.ui.BaseConfigScreen;

import dev.rdh.createunlimited.Util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import dev.rdh.createunlimited.CreateUnlimited;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import manifold.rt.api.NoBootstrap;

import static net.minecraftforge.fml.config.ModConfig.Type.*;

@NoBootstrap
public class CUConfigs {

	@ApiStatus.Internal
	public static final Map<Type, ConfigBase> CONFIGS = new EnumMap<>(Type.class);

	public static CUServer server() {
		return (CUServer) byType(SERVER);
	}

	public static @Nullable ConfigBase byType(Type type) {
		return CONFIGS.get(type);
	}

	public static @Nullable ForgeConfigSpec getSpecByType(Type type) {
		ConfigBase config = CONFIGS.get(type);
		return config != null ? config.specification : null;
	}

	private static <T extends ConfigBase> void register(Supplier<T> factory, Type side) {
		Pair<T, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(builder -> {
			T config = factory.get();
			config.registerAll(builder);
			return config;
		});

		T config = specPair.getLeft();
		config.specification = specPair.getRight();
		CONFIGS.put(side, config);
	}

	public static void register() {
		register(CUServer::new, SERVER);

		for(Entry<Type, ConfigBase> pair : CONFIGS.entrySet())
			Util.registerConfig(pair.getKey(), pair.getValue().specification);
	}

	public static void onLoad(ModConfig modConfig) {
		for (ConfigBase config : CONFIGS.values())
			if (config.specification == modConfig.getSpec())
				config.onLoad();
	}

	public static void onReload(ModConfig modConfig) {
		for (ConfigBase config : CONFIGS.values())
			if (config.specification == modConfig.getSpec())
				config.onReload();
	}

	public static BaseConfigScreen createConfigScreen(Screen parent) {
		initBCS();
		return new BaseConfigScreen(parent, CreateUnlimited.ID);
	}

	private static boolean done = false;

	private static void initBCS() {
		if(done) return;
		BaseConfigScreen.setDefaultActionFor(CreateUnlimited.ID, (base) ->
			base.withSpecs(getSpecByType(CLIENT), getSpecByType(COMMON), getSpecByType(SERVER))
				.withTitles("", "", "Settings")
		);
		done = true;
	}
	public static BaseConfigScreen createConfigScreen(@Nullable Minecraft mc, Screen parent) {
		return createConfigScreen(parent);
	}
}