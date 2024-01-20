package dev.rdh.createunlimited.fabric;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;

import com.mojang.brigadier.arguments.ArgumentType;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.rdh.createunlimited.CreateUnlimited;
import dev.rdh.createunlimited.multiversion.SupportedMinecraftVersion;

import manifold.rt.api.NoBootstrap;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.commands.CommandSourceStack;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;

import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

@NoBootstrap
public class UtilImpl {

	public static void registerCommand(LiteralArgumentBuilder<CommandSourceStack> command) {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, selection) -> dispatcher.register(command));
	}

	private static MethodHandle modLoadingContextRegisterConfig;
	private static MethodHandle forgeConfigRegistryRegister;
	private static Object forgeConfigRegistryInstance;

	private static void setupConfigRegistry() {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		try {
			if(SupportedMinecraftVersion.v1_19_2.isCurrentOrOlder()) {
				Class<?> modLoadingContextClass = Class.forName("net.minecraftforge.api.ModLoadingContext");
				modLoadingContextRegisterConfig = lookup.findStatic(modLoadingContextClass, "registerConfig",
					MethodType.methodType(ModConfig.class, String.class, Type.class, IConfigSpec.class));
			}

			if(SupportedMinecraftVersion.v1_20_1.isCurrentOrNewer()) {
				Class<?> forgeConfigRegistryClass = Class.forName("fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry");
				forgeConfigRegistryRegister = lookup.findVirtual(forgeConfigRegistryClass, "register",
					MethodType.methodType(ModConfig.class, String.class, Type.class, IConfigSpec.class));
				forgeConfigRegistryInstance = forgeConfigRegistryClass.getField("INSTANCE").get(null);
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static void registerConfig(ModConfig.Type type, IConfigSpec<?> spec) {
		if(modLoadingContextRegisterConfig == null && forgeConfigRegistryRegister == null) {
			setupConfigRegistry();
		}
		try {
			if(SupportedMinecraftVersion.v1_19_2.isCurrentOrOlder()) {
				ModConfig ignore = (ModConfig) modLoadingContextRegisterConfig.invokeExact(CreateUnlimited.ID, type, spec);
			}

			if(SupportedMinecraftVersion.v1_20_1.isCurrentOrNewer()) {
				//cannot use invokeExact because the instance class only exists in 1.20.1
				forgeConfigRegistryRegister.invoke(forgeConfigRegistryInstance, CreateUnlimited.ID, type, spec);
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>>
	void registerArgument(Class<A> clazz, I info, ResourceLocation id) {
		ArgumentTypeRegistry.registerArgumentType(id, clazz, info);
	}

	public static String getVersion(String modid) {
		return FabricLoader.getInstance()
			.getModContainer(modid)
			.orElseThrow(() -> new IllegalArgumentException("Mod container for \"" + modid + "\" not found"))
			.getMetadata()
			.getVersion()
			.getFriendlyString();
	}

	public static boolean isDevEnv() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	public static Attribute getReachAttribute() {
		return ReachEntityAttributes.REACH;
	}

	public static String platformName() {
		return FabricLoader.getInstance().isModLoaded("quilt_loader") ? "Quilt" : "Fabric";
	}
}
