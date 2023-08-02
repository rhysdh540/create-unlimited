package dev.rdh.createunlimited;

import com.simibubi.create.Create;

import dev.rdh.createunlimited.config.CUConfig;

import dev.rdh.createunlimited.config.command.CUCommands;
import dev.rdh.createunlimited.config.command.EnumArgument;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.fml.config.ModConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUnlimited {
	public static final String ID = "createunlimited";
	public static final String NAME = "Create Unlimited";
	public static final String VERSION = "0.5.0";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static void init() {
		LOGGER.info("{} v{} initializing! Create version: {} on platform: {}", NAME, VERSION, Create.VERSION, Util.platformName());

		EnumArgument.init();
		Util.registerConfig(ID, ModConfig.Type.SERVER, CUConfig.SPEC, "createunlimited.toml");
		CUConfig.init(Util.getConfigDirectory().resolve("createunlimited-IGNOREME.toml"));

		CUCommands.registerConfigCommand();
    }

	public static ResourceLocation asResource(String path) {
		return new ResourceLocation(ID, path);
	}
}
