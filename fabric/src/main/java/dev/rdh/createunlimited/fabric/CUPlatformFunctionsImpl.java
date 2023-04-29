package dev.rdh.createunlimited.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class CUPlatformFunctionsImpl {
	public static String platformName() {
		return FabricLoader.getInstance().isModLoaded("quilt_loader") ? "Quilt" : "Fabric";
	}
	public static Path getConfigDirectory() {
		return FabricLoader.getInstance().getConfigDir();
	}
    public static boolean isClientEnv() {
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }
    public static boolean isModLoaded(String modid) {
		return FabricLoader.getInstance().isModLoaded(modid);
    }
}
