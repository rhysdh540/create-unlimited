package dev.rdh.createunlimited.multiversion;

import dev.rdh.createunlimited.Util;

public enum SupportedMinecraftVersion {
	v1_20_1,
	v1_21_1,
	;

	public static final SupportedMinecraftVersion CURRENT = current();

	private static SupportedMinecraftVersion current() {
		String mcVersion = Util.INSTANCE.getVersion("minecraft");
		for(SupportedMinecraftVersion version : values()) {
			if(version.toString().equals(mcVersion)) {
				return version;
			}
		}

		throw new IllegalStateException("Unsupported Minecraft version: " + mcVersion);
	}

	@Override
	public String toString() {
		return name().substring(1).replace('_', '.');
	}

	@SuppressWarnings("unchecked")
	public static <T extends Throwable> RuntimeException unchecked(Throwable t) throws T {
		throw (T) t;
	}
}
