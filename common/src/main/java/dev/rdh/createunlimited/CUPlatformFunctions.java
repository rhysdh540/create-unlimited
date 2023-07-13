package dev.rdh.createunlimited;


import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.architectury.injectables.annotations.ExpectPlatform;

import net.minecraft.commands.CommandSourceStack;

import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;

public class CUPlatformFunctions {

	@ExpectPlatform
	public static String platformName() {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static Path getConfigDirectory() {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void registerCommand(LiteralArgumentBuilder<CommandSourceStack> command) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void registerConfig(String id, ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
		throw new AssertionError();
	}
}
