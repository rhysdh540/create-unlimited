package dev.rdh.createunlimited;

import com.simibubi.create.Create;

import dev.rdh.createunlimited.config.CUConfigs;

import dev.rdh.createunlimited.config.command.EnumArgument;

import manifold.rt.api.NoBootstrap;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoBootstrap
public abstract class CreateUnlimited {
	public static final String ID = "createunlimited";
	public static final String NAME = "Create Unlimited";
	public static final String VERSION = Util.getVersion(ID).split("-build")[0];
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static void init() {
		LOGGER.info("{} v{} initializing! Create version: {} on platform: {}",
				NAME, VERSION, Create.VERSION, Util.platformName());

		EnumArgument.init();
		CUConfigs.register();
    }

	public static ResourceLocation asResource(String path) {
		return new ResourceLocation(ID, path);
	}
}
