package dev.rdh.createunlimited.forge;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.rdh.createunlimited.CreateUnlimited;

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

import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import manifold.rt.api.NoBootstrap;

#if MC_1_19_2
@SuppressWarnings("UnstableApiUsage")
#endif
@NoBootstrap
public class UtilImpl {

	@ApiStatus.Internal
	public static Set<LiteralArgumentBuilder<CommandSourceStack>> commands = new HashSet<>();

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

	public static Attribute getReachAttribute() {
		#if POST_CURRENT_MC_1_20_1
		return ForgeMod.BLOCK_REACH.get();
		#elif PRE_CURRENT_MC_1_19_2
		return ForgeMod.REACH_DISTANCE.get();
		#endif
	}

	public static String platformName() {
		return "Forge";
	}
}
