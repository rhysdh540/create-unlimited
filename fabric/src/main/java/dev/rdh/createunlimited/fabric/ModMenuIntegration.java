package dev.rdh.createunlimited.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import dev.rdh.createunlimited.config.CUConfigs;

import manifold.rt.api.NoBootstrap;

@NoBootstrap
public class ModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return CUConfigs::createConfigScreen;
	}
}