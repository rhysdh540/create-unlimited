package dev.rdh.createunlimited.asm;

import org.objectweb.asm.commons.*;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.ext.IExtension;
import org.spongepowered.asm.mixin.transformer.ext.ITargetClassContext;

import dev.rdh.createunlimited.CreateUnlimited;

import java.util.HashMap;
import java.util.Map;

public class FunnyMixinExtension implements IExtension {
	private Remapper remapper;

	public FunnyMixinExtension() {
		Map<String, String> map = new HashMap<>();
		remapper = new SimpleRemapper(map);
	}

	@Override
	public boolean checkActive(MixinEnvironment environment) {
		return true;
	}

	@Override
	public void preApply(ITargetClassContext context) {
		ClassNode classNode = context.classNode;
		if (!classNode.name.startsWith("dev/rdh/createunlimited")) {
			return;
		}

		if(context.classInfo.isMixin) {
			CreateUnlimited.LOGGER.info("{} is a mixin!", classNode.name);
		} else {
			CreateUnlimited.LOGGER.info("{} is not a mixin!", classNode.name);
		}

//		classNode.accept(new ClassRemapper(null, remapper));
	}

	@Override
	public void postApply(ITargetClassContext context) {

	}

	@Override
	public void export(MixinEnvironment env, String name, boolean force, ClassNode classNode) {

	}
}
