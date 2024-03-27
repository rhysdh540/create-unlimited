package dev.rdh.createunlimited.config;

import com.simibubi.create.foundation.config.ui.BaseConfigScreen;

import dev.rdh.createunlimited.Util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import dev.rdh.createunlimited.CreateUnlimited;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import org.jetbrains.annotations.Nullable;

import static net.minecraftforge.fml.config.ModConfig.Type.*;

public class CUConfigs {

	public static final CUServer server = new CUServer();

	public static void register() {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		server.registerAll(builder);
		Util.registerConfig(SERVER, server.specification = builder.build());
	}

	public static void onLoad(ModConfig modConfig) {
		if (server.specification == modConfig.getSpec())
			server.onLoad();
	}

	public static void onReload(ModConfig modConfig) {
		if(server.specification == modConfig.getSpec())
			server.onReload();
	}

	public static BaseConfigScreen createConfigScreen(Screen parent) {
		initBCS();
		return new BaseConfigScreen(parent, CreateUnlimited.ID);
	}

	private static boolean done = false;

	private static void initBCS() {
		if(done) return;
		BaseConfigScreen.setDefaultActionFor(CreateUnlimited.ID, (base) ->
			base.withSpecs(null, null, server.specification)
				.withTitles("", "", "Settings")
		);
		done = true;
	}

	public static BaseConfigScreen createConfigScreen(@Nullable @SuppressWarnings("unused") Minecraft mc, Screen parent) {
		return createConfigScreen(parent);
	}
}