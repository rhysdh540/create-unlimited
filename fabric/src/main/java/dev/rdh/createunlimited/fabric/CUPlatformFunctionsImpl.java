package dev.rdh.createunlimited.fabric;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;

import java.nio.file.Path;

public class CUPlatformFunctionsImpl {

	public static String platformName() {
		return FabricLoader.getInstance().isModLoaded("quilt_loader") ? "Quilt" : "Fabric";
	}

	public static Path getConfigDirectory() {
		return FabricLoader.getInstance().getConfigDir();
	}

	public static void registerCommand(LiteralArgumentBuilder<CommandSourceStack> command) {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, selection) -> dispatcher.register(command));
	}
}
