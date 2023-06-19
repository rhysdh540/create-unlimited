package dev.rdh.createunlimited;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.commands.CommandSourceStack;

import java.nio.file.Path;

/**
 * This class contains functions that are implemented differently on each platform. They use the {@code @ExpectPlatform} annotation to tell Architectury that they are implemented differently on each platform.
 * Architectury will then replace these functions with the correct ones depending on the modloader, which will be used at runtime.
 * @see dev.rdh.createunlimited.fabric.CUPlatformFunctionsImpl Fabric Implementations
 * @see dev.rdh.createunlimited.forge.CUPlatformFunctionsImpl Forge Implementations
 */
public class CUPlatformFunctions {
	/**
	 * Gets the current modloader, as a String.
	 * @return the current modloader that Minecraft is being run with.
	 * @see dev.rdh.createunlimited.forge.CUPlatformFunctionsImpl#platformName() Forge Implementation
	 * @see dev.rdh.createunlimited.fabric.CUPlatformFunctionsImpl#platformName() Fabric Implementation
	 */
	@ExpectPlatform
	public static String platformName() {
		throw new AssertionError();
	}

	/**
	 * Gets the directory where the current modloader's configuration files are stored.
	 * <p>
	 * The directory is returned as a {@code Path} object.
	 * For servers, it is stored in the world save folder, and for clients, it is stored in the root game directory.
	 * @return a {@code Path} object representing the directory where the current modloader's configuration files are stored.
	 * @see dev.rdh.createunlimited.forge.CUPlatformFunctionsImpl#getConfigDirectory() Forge Implementation
	 * @see dev.rdh.createunlimited.fabric.CUPlatformFunctionsImpl#getConfigDirectory() Fabric Implementation
	 */
	@ExpectPlatform
	public static Path getConfigDirectory() {
		throw new AssertionError();
	}

	/**
	 * Registers a command with the current modloader's command dispatcher.
	 * @param command the command to register
	 * @see dev.rdh.createunlimited.forge.CUPlatformFunctionsImpl#registerCommand(LiteralArgumentBuilder) Forge Implementation
	 * @see dev.rdh.createunlimited.fabric.CUPlatformFunctionsImpl#registerCommand(LiteralArgumentBuilder) Fabric Implementation
	 */
	@ExpectPlatform
	public static void registerCommand(LiteralArgumentBuilder<CommandSourceStack> command) {
		throw new AssertionError();
	}
}
