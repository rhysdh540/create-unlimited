package dev.rdh.createunlimited;

import org.objectweb.asm.tree.*;

import java.util.Set;

import static org.objectweb.asm.Opcodes.*;

public final class Asm {
	public static void instrumentTrackPlacement(ClassNode targetClass, String targetClassName) {
		Set<String> targetMessages = Set.of(
			"perpendicular", "ascending_s_curve", "too_sharp", "slope_turn", "opposing_slopes",
			"leave_slope_ascending", "leave_slope_descending", "too_steep", "turn_90"
		);

		MethodNode tryConnect = targetClass.methods.stream()
			.filter(m -> m.name.equals("tryConnect"))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Could not find tryConnect method in " + targetClassName));

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
		headInject.add(new VarInsnNode(ISTORE, lvtIndex)); // store result in the last LVT index
		tryConnect.instructions.insert(headInject);

		for(int i = 0; i < tryConnect.instructions.size(); i++) {
			AbstractInsnNode insn = tryConnect.instructions.get(i);
			if(insn.getOpcode() != ARETURN) continue;

			LdcInsnNode ldc = findPreviousNode(insn, LdcInsnNode.class);
			if(ldc == null) continue;
			if(!(ldc.cst instanceof String message)) continue;
			if(!targetMessages.contains(message)) continue;

			// now we know that we should inject here
			JumpInsnNode jump = findPreviousNode(ldc, JumpInsnNode.class);
			if(jump == null) continue;
			LabelNode toInject = findPreviousNode(jump, LabelNode.class);
			tryConnect.instructions.insert(toInject, new JumpInsnNode(IFEQ, jump.label));
			tryConnect.instructions.insert(toInject, new VarInsnNode(ILOAD, lvtIndex));
			i += 2;
		}
	}

	private static <T extends AbstractInsnNode> T findPreviousNode(AbstractInsnNode node, Class<T> type) {
		while((node = node.getPrevious()) != null) {
			if(type.isInstance(node)) return type.cast(node);
		}
		return null;
	}
}
