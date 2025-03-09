package dev.rdh.createunlimited.forge;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.rdh.createunlimited.CreateUnlimited;
import dev.rdh.createunlimited.Util;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;

import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.entity.ai.attributes.Attribute;

import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.List;

@SuppressWarnings({"UnstableApiUsage", "RedundantSuppression"})
public class UtilImpl implements Util {

	public void registerConfig(ModConfig.Type type, IConfigSpec<?> spec) {
		ModLoadingContext.get().registerConfig(type, spec);
	}

	public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>>
	void registerArgument(Class<A> clazz, I info, ResourceLocation id) {
		CreateUnlimitedForge.ARGUMENTS.register(id.getPath(), () -> ArgumentTypeInfos.registerByClass(clazz, info));
	}

	@Override
	public String getVersion(String modid) {
		String versionString = "UNKNOWN";

		List<IModInfo> infoList = ModList.get().getModFileById(modid).getMods();
		if (infoList.size() > 1) {
			CreateUnlimited.LOGGER.error("Multiple mods for ID: " + modid);
		}
		for (IModInfo info : infoList) {
			if (info.getModId().equals(modid)) {
				versionString = info.getVersion().toString();
				break;
			}
		}
		return versionString;
	}

	@Override
	public boolean isDevEnv() {
		return !FMLLoader.isProduction();
	}

	@Override
	public Attribute getReachAttribute() {
		return ForgeMod.BLOCK_REACH.get();
	}

	@Override
	public String platformName() {
		return "Forge";
	}
}
