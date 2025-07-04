package dev.rdh.createunlimited;

import dev.rdh.createunlimited.config.CUConfig;

import dev.rdh.createunlimited.command.EnumArgument;
import dev.rdh.createunlimited.multiversion.SupportedMinecraftVersion;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.api.contraption.BlockMovementChecks.CheckResult;

public interface CreateUnlimited {
	String ID = "createunlimited";
	String NAME = "Create Unlimited";
	String VERSION = Util.getVersion(ID).split("-build")[0];
	Logger LOGGER = LoggerFactory.getLogger(NAME);

    default void init() {
		LOGGER.info("{} v{} initializing on platform: {}!",
			NAME, VERSION, Util.platformName());

		LOGGER.info("Detected Minecraft version: {}", SupportedMinecraftVersion.CURRENT);

		EnumArgument.init();
		CUConfig.register();

		BlockMovementChecks.registerMovementAllowedCheck((state, level, pos) -> {
			if (CUConfig.getOrFalse(CUConfig.instance.allowContraptionMoveAllow)) {
				return CheckResult.SUCCESS;
			} else {
				return CheckResult.PASS;
			}
		});
    }

	static ResourceLocation asResource(String path) {
		return new ResourceLocation(ID, path);
	}
}
