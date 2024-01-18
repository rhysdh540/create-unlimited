package dev.rdh.createunlimited.multiversion;

import manifold.rt.api.NoBootstrap;

import dev.rdh.createunlimited.Util;

@NoBootstrap
public enum SupportedMinecraftVersion {
	v1_18_2,
	v1_19_2,
	v1_20_1,

	;

	public static final SupportedMinecraftVersion CURRENT = current();

	private static SupportedMinecraftVersion current() {
		String mcVersion = Util.getVersion("minecraft");
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

	public boolean isCurrent() {
		return this == CURRENT;
	}

	// i feel like these should be the other way around but that breaks it for some reason
	public boolean isCurrentOrNewer() {
		return this.ordinal() <= CURRENT.ordinal();
	}

	public boolean isCurrentOrOlder() {
		return this.ordinal() >= CURRENT.ordinal();
	}
}
