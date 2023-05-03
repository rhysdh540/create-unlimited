package dev.rdh.createunlimited.fabric;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;

import java.nio.file.Path;

/**
 * This class holds the Fabric implementations of the functions declared in {@code CUPlatformFunctions}.
 * @see dev.rdh.createunlimited.CUPlatformFunctions CUPlatformFunctions
 */
public class CUPlatformFunctionsImpl {
	/**
	 * Gets the current modloader, as a String.
	 * @return Quilt if Quilt Loader is installed, Fabric otherwise.
	 * @see dev.rdh.createunlimited.CUPlatformFunctions#platformName() CUPlatformFunctions.platformName()
	 */
	public static String platformName() {
		return FabricLoader.getInstance().isModLoaded("quilt_loader") ? "Quilt" : "Fabric";
	}
/**
 * Gets the directory where Fabric's configuration files are stored.
 * <p>
 * The directory is returned as a {@code Path} object.
 * For servers, it is stored in the world save folder, and for clients, it is stored in the root game directory.
 * @return a {@code Path} object representing the directory where the current modloader's configuration files are stored.
 * @see dev.rdh.createunlimited.CUPlatformFunctions#getConfigDirectory() CUPlatformFunctions.getConfigDirectory()
 */
	public static Path getConfigDirectory() {
		return FabricLoader.getInstance().getConfigDir();
	}

	/**
	 * Registers a command with the Fabric command dispatcher.
	 * @param command the command to register
	 * @see dev.rdh.createunlimited.CUPlatformFunctions#registerCommand(LiteralArgumentBuilder) CUPlatformFunctions.registerCommand(LiteralArgumentBuilder)
	 */
	public static void registerCommand(LiteralArgumentBuilder<CommandSourceStack> command) {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, selection) -> dispatcher.register(command));
	}
}
