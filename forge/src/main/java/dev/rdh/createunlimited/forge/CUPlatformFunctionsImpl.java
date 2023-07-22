package dev.rdh.createunlimited.forge;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CUPlatformFunctionsImpl {

	public static List<LiteralArgumentBuilder<CommandSourceStack>> commands = new ArrayList<>();

	public static String platformName() {
		return "Forge";
	}

	public static Path getConfigDirectory() {
		return FMLPaths.CONFIGDIR.get();
	}

    public static void registerCommand(LiteralArgumentBuilder<CommandSourceStack> command) {
		commands.add(command);
    }

	public static void registerConfig(String id, ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
		ModLoadingContext.get().registerConfig(type, spec, fileName);
	}

	public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>>
	void registerArgument(String name, Class<A> clazz, I info, ResourceLocation id) {
		ArgumentTypeInfos.registerByClass(clazz, info);
	}
}
