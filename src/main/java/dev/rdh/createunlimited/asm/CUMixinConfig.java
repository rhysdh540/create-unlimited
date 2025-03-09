package dev.rdh.createunlimited.asm;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;

import com.llamalad7.mixinextras.utils.MixinInternals;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class CUMixinConfig implements IMixinConfigPlugin {
	@Override
	public void onLoad(String mixinPackage) {
		MixinExtrasBootstrap.init();
		MixinInternals.registerExtension(new FunnyMixinExtension());
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return true;
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		if(targetClassName.equals("com.simibubi.create.content.trains.track.TrackPlacement")) {
			Asm.instrumentTrackPlacement(targetClass);
		}
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

	@Override
	public String getRefMapperConfig() {
		return null;
	}
}
