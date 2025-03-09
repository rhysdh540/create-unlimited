package dev.rdh.createunlimited.fabric;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.rdh.createunlimited.CreateUnlimited;
import dev.rdh.createunlimited.Util;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;

import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static dev.rdh.createunlimited.multiversion.SupportedMinecraftVersion.*;

public class UtilImpl implements Util {

	@Override
	public void registerConfig(ModConfig.Type type, IConfigSpec<?> spec) {
		ForgeConfigRegistry.INSTANCE.register(CreateUnlimited.ID, type, spec);
	}

	@Override
	public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>>
	void registerArgument(Class<A> clazz, I info, ResourceLocation id) {
		ArgumentTypeRegistry.registerArgumentType(id, clazz, info);
	}

	@Override
	public String getVersion(String modid) {
		return FabricLoader.getInstance()
			.getModContainer(modid)
			.orElseThrow(() -> new IllegalArgumentException("Mod container for \"" + modid + "\" not found"))
			.getMetadata()
			.getVersion()
			.getFriendlyString();
	}

	@Override
	public boolean isDevEnv() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	@Override
	public Attribute getReachAttribute() {
		return ReachEntityAttributes.REACH;
	}

	@Override
	public String platformName() {
		return FabricLoader.getInstance().isModLoaded("quilt_loader") ? "Quilt" : "Fabric";
	}
}
