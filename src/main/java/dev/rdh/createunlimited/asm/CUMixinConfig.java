package dev.rdh.createunlimited.asm;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import dev.rdh.createunlimited.boot.Transformer;

import java.util.List;
import java.util.Set;

public final class CUMixinConfig extends Transformer {
	@Override
	public void onLoad(String mixinPackage) {
		super.onLoad(mixinPackage);
		MixinExtrasBootstrap.init();
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		super.postApply(targetClassName, targetClass, mixinClassName, mixinInfo);
		if(targetClassName.equals("com.simibubi.create.content.trains.track.TrackPlacement")) {
			Asm.instrumentTrackPlacement(targetClass);
		}
	}

}
