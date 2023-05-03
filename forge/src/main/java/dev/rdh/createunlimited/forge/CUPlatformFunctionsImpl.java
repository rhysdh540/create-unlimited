package dev.rdh.createunlimited.forge;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds the Forge implementations of the functions declared in {@code CUPlatformFunctions}.
 * @see dev.rdh.createunlimited.CUPlatformFunctions CUPlatformFunctions
 */
@SuppressWarnings("UnstableApiUsage")
public class CUPlatformFunctionsImpl {
	/**
	 * The list of commands that should be registered when the command registration event is fired. This is populated by the {@code registerCommand} method, and is used by the {@code registerCommands} method.
	 * @see dev.rdh.createunlimited.CUPlatformFunctions#registerCommand(LiteralArgumentBuilder) CUPlatformFunctions.registerCommand(LiteralArgumentBuilder)
	 * @see dev.rdh.createunlimited.forge.CreateUnlimitedForge#registerCommands(RegisterCommandsEvent) CreateUnlimitedForge.registerCommands(RegisterCommandsEvent)
	 */
	public static List<LiteralArgumentBuilder<CommandSourceStack>> commands = new ArrayList<>();
	/**
	 * Gets the current modloader, as a String
	 * @return Forge
	 * @see dev.rdh.createunlimited.CUPlatformFunctions#platformName() CUPlatformFunctions.platformName()
	 */
	public static String platformName() {
		return "Forge";
	}
	/**
	 * Gets the directory where Forge's configuration files are stored.
	 * <p>
	 * The directory is returned as a {@code Path} object.
	 * For servers, it is stored in the world save folder, and for clients, it is stored in the root game directory.
	 * @return a {@code Path} object representing the directory where the current modloader's configuration files are stored.
	 * @see dev.rdh.createunlimited.CUPlatformFunctions#getConfigDirectory() CUPlatformFunctions.getConfigDirectory()
	 */
	public static Path getConfigDirectory() {
		return FMLPaths.CONFIGDIR.get();
	}

	/**
	 * Adds a command to the list of commands that should be registered when the command registration event is fired.
	 * <p>
	 * Since commands cannot be registered on-demand like they can on Fabric, we have to store them in a list and register them all at once when the event is fired.
	 * @param command the command to add to the list
	 * @see dev.rdh.createunlimited.CUPlatformFunctions#registerCommand(LiteralArgumentBuilder) CUPlatformFunctions.registerCommand(LiteralArgumentBuilder)
	 */
    public static void registerCommand(LiteralArgumentBuilder<CommandSourceStack> command) {
		commands.add(command);
    }
}
