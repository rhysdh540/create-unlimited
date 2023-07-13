package dev.rdh.createunlimited.fabric;

import com.simibubi.create.foundation.config.ui.BaseConfigScreen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import dev.rdh.createunlimited.CreateUnlimited;

public class ModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> CreateUnlimited.createConfigScreen(null, screen);
	}
}
