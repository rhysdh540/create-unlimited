package net.rdh.createunlimited.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class CUExpectPlatformImpl {
	public static String platformName() {
		return FabricLoader.getInstance().isModLoaded("quilt_loader") ? "Quilt" : "Fabric";
	}
}
