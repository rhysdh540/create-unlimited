package dev.rdh.createunlimited.util.fabric;

import com.mojang.brigadier.arguments.ArgumentType;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.commands.CommandSourceStack;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;

import net.minecraft.resources.ResourceLocation;

#if PRE_CURRENT_MC_1_19_2
import net.minecraftforge.api.ModLoadingContext;
#elif POST_CURRENT_MC_1_20_1
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
#endif
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;

public class UtilImpl {

	public static String platformName() {
		return FabricLoader.getInstance().isModLoaded("quilt_loader") ? "Quilt" : "Fabric";
	}

	public static Path getConfigDirectory() {
		return FabricLoader.getInstance().getConfigDir();
	}

	public static void registerCommand(LiteralArgumentBuilder<CommandSourceStack> command) {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, selection) -> dispatcher.register(command));
	}

	public static void registerConfig(String id, ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
		#if PRE_CURRENT_MC_1_19_2
		ModLoadingContext.registerConfig(id, type, spec, fileName);
		#elif POST_CURRENT_MC_1_20_1
		ForgeConfigRegistry.INSTANCE.register(id, type, spec, fileName);
		#endif
	}

	public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>>
	void registerArgument(String name, Class<A> clazz, I info, ResourceLocation id) {
		ArgumentTypeRegistry.registerArgumentType(id, clazz, info);
	}
}
