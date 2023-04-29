package dev.rdh.createunlimited.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

@SuppressWarnings("UnstableApiUsage")
public class CUPlatformFunctionsImpl {
	public static String platformName() {
		return "Forge";
	}
	public static Path getConfigDirectory() {
		return FMLPaths.CONFIGDIR.get();
	}
	public static boolean isClientEnv() {
		return FMLEnvironment.dist.isClient();
	}
	public static boolean isModLoaded(String modid) {
		return ModList.get().isLoaded(modid);
	}

}
