package dev.rdh.createunlimited.forge;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.rdh.createunlimited.CreateUnlimited;
import dev.rdh.createunlimited.multiversion.SupportedMinecraftVersion;

import net.minecraft.commands.CommandSourceStack;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;

import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.entity.ai.attributes.Attribute;

import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import manifold.rt.api.NoBootstrap;

@NoBootstrap
@SuppressWarnings({"UnstableApiUsage", "RedundantSuppression"})
public class UtilImpl {

	static Set<LiteralArgumentBuilder<CommandSourceStack>> commands = new HashSet<>();

	public static void registerCommand(LiteralArgumentBuilder<CommandSourceStack> command) {
		commands.add(command);
	}

	public static void registerConfig(ModConfig.Type type, IConfigSpec<?> spec) {
		ModLoadingContext.get().registerConfig(type, spec);
	}

	public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>>
	void registerArgument(Class<A> clazz, I info, ResourceLocation id) {
		ArgumentTypeInfos.registerByClass(clazz, info);
	}

	public static String getVersion(String modid) {
		String versionString = "UNKNOWN";

		List<IModInfo> infoList = ModList.get().getModFileById(modid).getMods();
		if (infoList.size() > 1) {
			CreateUnlimited.LOGGER.error("Multiple mods for ID: " + modid);
		}
		for (IModInfo info : infoList) {
			if (info.getModId().equals(modid)) {
				versionString = MavenVersionStringHelper.artifactVersionToString(info.getVersion());
				break;
			}
		}
		return versionString;
	}

	public static boolean isDevEnv() {
		return !FMLLoader.isProduction();
	}

	private static final RegistryObject<Attribute> reachAttribute = makeReachAttribute();

	@SuppressWarnings({"unchecked", "JavaReflectionMemberAccess"})
	private static RegistryObject<Attribute> makeReachAttribute() {
		try {
			if(SupportedMinecraftVersion.v1_20_1.isCurrentOrNewer()) {
				return (RegistryObject<Attribute>) ForgeMod.class.getField("BLOCK_REACH").get(null);
			} else if(SupportedMinecraftVersion.v1_19_2.isCurrentOrOlder()) {
				return (RegistryObject<Attribute>) ForgeMod.class.getField("REACH_DISTANCE").get(null);
			}
			throw new IllegalStateException("Unsupported minecraft version: " + SupportedMinecraftVersion.CURRENT);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException("Failed to get reach attribute for minecraft version " + SupportedMinecraftVersion.CURRENT, e);
		}
	}

	public static Attribute getReachAttribute() {
		return reachAttribute.get();
	}

	public static String platformName() {
		return "Forge";
	}
}
