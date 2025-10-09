package dev.rdh.createunlimited.asm;

import org.objectweb.asm.tree.ClassNode;

import dev.rdh.createunlimited.Util;
import dev.rdh.createunlimited.boot.Transformer;

import java.util.Set;

public final class CUMixinConfig extends Transformer {

	@Override
	public void onLoad(String mixinPackage) {
		super.onLoad(mixinPackage);

		earlyBoot("dev.rdh.createunlimited.asm.Asm");
	}

	@Override
	protected String getPlatform() {
		try {
			Class.forName("net.minecraftforge.fml.loading.FMLLoader");
			return "1.20.1-forge";
		} catch (ClassNotFoundException e) {
			// not forge
		}

		try {
			Class.forName("net.neoforged.fml.loading.FMLLoader");
			return "1.21.1-neoforge";
		} catch (ClassNotFoundException e) {
			// not neo
		}

		try {
			Class.forName("net.fabricmc.loader.api.FabricLoader");
			String mcVersion = Util.getVersion("minecraft");
			if (mcVersion.equals("1.20.1")) {
				return "1.20.1-fabric";
			} else {
				throw new RuntimeException("Unsupported Minecraft version for Fabric: " + mcVersion);
			}
		} catch (ClassNotFoundException e) {
			// not fabric
		}

		throw new RuntimeException("Could not determine platform!");
	}

	@Override
	public Set<String> getTransformedClasses() {
		Set<String> c = super.getTransformedClasses();
		c.add("com.copycatsplus.copycats.foundation.copycat.ICopycatBlock");
		return c;
	}

	@Override
	public void transform(ClassNode node) {
		super.transform(node);
		if(node.name.equals("com/copycatsplus/copycats/foundation/copycat/ICopycatBlock")) {
			Asm.instrumentICopycatBlock(node);
		}

		if(node.name.equals("com/simibubi/create/content/trains/track/TrackPlacement")) {
			Asm.instrumentTrackPlacement(node);
		}
	}
}
