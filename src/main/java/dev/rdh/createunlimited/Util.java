package dev.rdh.createunlimited;

import com.mojang.brigadier.arguments.ArgumentType;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.entity.ai.attributes.Attribute;

import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import xyz.wagyourtail.unimined.expect.annotation.ExpectPlatform;

public abstract class Util {

	@ExpectPlatform
	public static String getVersion(String modid) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static boolean isDevEnv() {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static String platformName() {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void registerConfig(ModConfig.Type type, IConfigSpec<?> spec) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>>
	void registerArgument(Class<A> clazz, I info, ResourceLocation id) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static Attribute getReachAttribute() {
		throw new AssertionError();
	}
}
