package dev.rdh.createunlimited.asm;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.ext.Extensions;
import org.spongepowered.asm.mixin.transformer.ext.IExtension;
import org.spongepowered.asm.mixin.transformer.ext.ITargetClassContext;

import dev.rdh.createunlimited.Reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CUMixinConfig implements IMixinConfigPlugin, IExtension {
	@Override
	public void onLoad(String mixinPackage) {
		IMixinTransformer transformer = (IMixinTransformer) MixinEnvironment.getCurrentEnvironment().getActiveTransformer();
		Extensions extensions = (Extensions) transformer.getExtensions();
		extensions.add(this);
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
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
		Class<?> myTargetsClass = myTargets.getClass();

		Map<String, List<?>> backingTargets = null;
		for (Field field : myTargetsClass.getDeclaredFields()) {
			if (Map.class.isAssignableFrom(field.getType()) && field.isSynthetic()) {
				backingTargets = Reflection.getField(field, myTargets);
				break;
			}
		}

		if (backingTargets == null) {
			StringBuilder sb = new StringBuilder("Failed to find backing map of Set class ")
				.append(myTargetsClass.getName()).append('\n').append("Fields:").append('\n');
			for (Field field : myTargetsClass.getDeclaredFields()) {
				sb.append('\t').append(field.getName()).append(": ").append(field.getType().getName());
				if (field.isSynthetic()) {
					sb.append(" (synthetic)");
				}
				sb.append('\n');
			}

			throw Reflection.unchecked(new NoSuchFieldException(sb.toString()));
		}

		backingTargets.putIfAbsent("com.copycatsplus.copycats.foundation.copycat.ICopycatBlock", new ArrayList<>());
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean checkActive(MixinEnvironment environment) {
		return true;
	}

	@Override
	public void preApply(ITargetClassContext context) {
		if (context.getClassInfo().getClassName().equals("com.copycatsplus.copycats.foundation.copycat.ICopycatBlock")) {
			Asm.instrumentICopycatBlock(context.getClassNode());
		}
	}

	@Override
	public void postApply(ITargetClassContext context) {

	}

	@Override
	public void export(MixinEnvironment env, String name, boolean force, ClassNode classNode) {

	}
}
