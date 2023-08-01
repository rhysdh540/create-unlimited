package dev.rdh.createunlimited;

import com.simibubi.create.Create;

import dev.rdh.createunlimited.config.CUConfig;

import dev.rdh.createunlimited.config.command.CUCommands;
import dev.rdh.createunlimited.config.command.EnumArgument;
import dev.rdh.createunlimited.util.Util;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.fml.config.ModConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUnlimited {
	public static final String ID = "createunlimited";
	public static final String NAME = "Create Unlimited";
	public static final String VERSION = "0.4.1";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static void init() {
		LOGGER.info("{} initializing! Create version: {} on platform: {}", NAME, Create.VERSION, Util.platformName());

		Util.registerConfig(ID, ModConfig.Type.SERVER, CUConfig.SPEC, "createunlimited.toml");
		CUConfig.init(Util.getConfigDirectory().resolve("createunlimited-IGNOREME.toml"));

		CUCommands.registerConfigCommand();
		Util.registerArgument("enumargument", EnumArgument.class, new EnumArgument.Info(), asResource("enumargument"));
    }

	public static ResourceLocation asResource(String path) {
		return new ResourceLocation(ID, path);
	}
}
