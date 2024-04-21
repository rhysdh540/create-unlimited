package dev.rdh.createunlimited;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.objectweb.asm.Opcodes.*;

public class CUMixinConfig implements IMixinConfigPlugin {
	@Override
	public void onLoad(String mixinPackage) {
		MixinExtrasBootstrap.init();
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
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		if(!targetClassName.equals("com.simibubi.create.content.trains.track.TrackPlacement")) {
			return;
		}

		Set<String> targetMessages = Set.of(
			"perpendicular", "ascending_s_curve", "too_sharp", "slope_turn", "opposing_slopes",
			"leave_slope_ascending", "leave_slope_descending", "too_steep", "turn_90"
		);

		MethodNode tryConnect = targetClass.methods.stream()
			.filter(m -> m.name.equals("tryConnect"))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Could not find tryConnect method in " + targetClassName));

		// the plan:
		// first inject our enabled check at the top of the method into the LVT
		// find all ARETURN instructions where the previous line (or the one before that if the previous one calls tooJumbly())
		// calls withMessage and said message is in the list (for example "perpendicular")
		// go to the line before that, make sure the last instruction is a jump instruction, get the line number of that instruction
		// then create a new line after it that loads enabled from earlier and jumps to the line number from before if it's false

		// still broken: 45° sharp turns, sloped s bends, and some very sharp 90° turns

		String playerClassName = tryConnect.localVariables.stream().filter(node -> node.index == 1)
			.findFirst().orElseThrow().desc;
		int lvtIndex = tryConnect.localVariables.size();

		InsnList headInject = new InsnList();
		headInject.add(new FieldInsnNode(GETSTATIC, "dev/rdh/createunlimited/config/CUConfigs", "server", "Ldev/rdh/createunlimited/config/CUServer;"));
		headInject.add(new FieldInsnNode(GETFIELD, "dev/rdh/createunlimited/config/CUServer", "placementChecks", "Lcom/simibubi/create/foundation/config/ConfigBase$ConfigEnum;"));
		headInject.add(new FieldInsnNode(GETSTATIC, "dev/rdh/createunlimited/config/CUServer$PlacementCheck", "ON", "Ldev/rdh/createunlimited/config/CUServer$PlacementCheck;"));
		headInject.add(new MethodInsnNode(INVOKESTATIC, "dev/rdh/createunlimited/Util", "orElse", "(Lcom/simibubi/create/foundation/config/ConfigBase$CValue;Ljava/lang/Object;)Ljava/lang/Object;"));
		headInject.add(new TypeInsnNode(CHECKCAST, "dev/rdh/createunlimited/config/CUServer$PlacementCheck"));
		headInject.add(new VarInsnNode(ALOAD, 1)); // load player (second argument)
		headInject.add(new MethodInsnNode(INVOKEVIRTUAL, "dev/rdh/createunlimited/config/CUServer$PlacementCheck", "isEnabledFor", "(" + playerClassName + ")Z"));
		headInject.add(new VarInsnNode(ISTORE, lvtIndex)); // store result in local variable 100 (which is not used in the method)
		tryConnect.instructions.insert(headInject);

		for(int i = 0; i < tryConnect.instructions.size(); i++) {
			AbstractInsnNode insn = tryConnect.instructions.get(i);
			if(insn.getOpcode() != ARETURN) continue;
			System.out.println("Found ARETURN instruction");

			LdcInsnNode ldc = findPrevious(insn, LdcInsnNode.class);
			if(ldc == null) continue;
			if(!(ldc.cst instanceof String message)) continue;
			if(!targetMessages.contains(message)) continue;
			System.out.println("Found message: " + message);

			// now we know that we should inject here
			JumpInsnNode jump = findPrevious(ldc, JumpInsnNode.class);
			if(jump == null) continue;
			System.out.println("Found jump instruction: " + jump.label.label);
			tryConnect.instructions.insert(jump, new JumpInsnNode(IFEQ, jump.label));
			tryConnect.instructions.insert(jump, new VarInsnNode(ILOAD, lvtIndex));

			i += 2;
		}

		ClassWriter cw = new ClassWriter(0);
		targetClass.accept(cw);
		try {
			Files.write(Path.of("TrackPlacement.class"), cw.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static <T extends AbstractInsnNode> T findPrevious(AbstractInsnNode node, Class<T> type) {
		while((node = node.getPrevious()) != null) {
			if(type.isInstance(node)) return type.cast(node);
		}
		return null;
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

	@Override
	public String getRefMapperConfig() {
		return null;
	}
}
