package dev.rdh.createunlimited.forge;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
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
}
