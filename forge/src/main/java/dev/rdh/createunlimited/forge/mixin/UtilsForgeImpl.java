package dev.rdh.createunlimited.forge.mixin;

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
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import dev.rdh.createunlimited.*;

import dev.rdh.createunlimited.forge.Events;
import java.nio.file.Path;
import java.util.List;

import manifold.rt.api.NoBootstrap;

#if MC_1_19_2
@SuppressWarnings({ "UnstableApiUsage", "OverwriteAuthorRequired" })
#else
@SuppressWarnings({ "OverwriteAuthorRequired" })
#endif
@NoBootstrap
@Mixin(Utils.class)
public class UtilsForgeImpl {

	@Overwrite
	public static Path getConfigDirectory() {
		return FMLPaths.CONFIGDIR.get();
	}

	@Overwrite
	public static void registerCommand(LiteralArgumentBuilder<CommandSourceStack> command) {
		Events.commands.add(command);
	}

	@Overwrite
	public static void registerConfig(ModConfig.Type type, IConfigSpec<?> spec) {
		ModLoadingContext.get().registerConfig(type, spec);
	}

	@Overwrite
	public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>>
	void registerArgument(Class<A> clazz, I info, ResourceLocation id) {
		ArgumentTypeInfos.registerByClass(clazz, info);
	}

	@Overwrite
	public static String getVersion() {
		String versionString = "UNKNOWN";

		List<IModInfo> infoList = ModList.get().getModFileById(CreateUnlimited.ID).getMods();
		if (infoList.size() > 1) {
			CreateUnlimited.LOGGER.error("Multiple mods for ID: " + CreateUnlimited.ID);
		}
		for (IModInfo info : infoList) {
			if (info.getModId().equals(CreateUnlimited.ID)) {
				versionString = MavenVersionStringHelper.artifactVersionToString(info.getVersion());
				break;
			}
		}
		return versionString;
	}

	@Overwrite
	public static boolean isDevEnv() {
		return !FMLLoader.isProduction();
	}

	@Overwrite
	public static Attribute getReachAttribute() {
		#if POST_CURRENT_MC_1_20_1
		return ForgeMod.BLOCK_REACH.get();
		#elif PRE_CURRENT_MC_1_19_2
		return ForgeMod.REACH_DISTANCE.get();
		#endif
	}

	@Overwrite
	public static String platformName() {
		return "Forge";
	}
}
