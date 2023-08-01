package dev.rdh.createunlimited.util;

import com.mojang.brigadier.arguments.ArgumentType;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.architectury.injectables.annotations.ExpectPlatform;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;

public class PlatformUtils {

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

	@ExpectPlatform
	public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>>
	void registerArgument(String name, Class<A> clazz, I info, ResourceLocation id) {
		throw new AssertionError();
	}
}
