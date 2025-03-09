package dev.rdh.createunlimited;

import com.simibubi.create.CreateBuildInfo;

import dev.rdh.createunlimited.config.CUConfig;

import dev.rdh.createunlimited.command.EnumArgument;
import dev.rdh.createunlimited.multiversion.SupportedMinecraftVersion;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface CreateUnlimited {
	String ID = "createunlimited";
	String NAME = "Create Unlimited";
	String VERSION = Util.INSTANCE.getVersion(ID).split("-build")[0];
	Logger LOGGER = LoggerFactory.getLogger(NAME);

    default void init() {
		LOGGER.info("{} v{} initializing! Create version: {} on platform: {}",
				NAME, VERSION, CreateBuildInfo.VERSION, Util.INSTANCE.platformName());

		LOGGER.info("Detected Minecraft version: {}", SupportedMinecraftVersion.CURRENT);

		EnumArgument.init();
		CUConfig.register();
    }

	static ResourceLocation asResource(String path) {
		return new ResourceLocation(ID, path);
	}
}
