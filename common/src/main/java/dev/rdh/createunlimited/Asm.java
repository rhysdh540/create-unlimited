package dev.rdh.createunlimited;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.objectweb.asm.Opcodes.*;

public final class Asm {

	public static void instrumentTrackPlacement(ClassNode targetClass) {
		if(!targetClass.name.equals("com/simibubi/create/content/trains/track/TrackPlacement")) {
			String caller = Thread.currentThread().getStackTrace()[2].getClassName();
			throw new RuntimeException("instrumentTrackPlacement called from \"" + caller + "\" with wrong target class: " + targetClass.name);
		}

		MethodNode tryConnect = targetClass.methods.stream()
			.filter(m -> m.name.equals("tryConnect"))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Could not find tryConnect method in TrackPlacement"));

		String playerClassName = tryConnect.localVariables.stream().filter(node -> node.index == 1)
			.findFirst().orElseThrow().desc;
		int lvtIndex = tryConnect.localVariables.size();

		tryConnect.instructions.insert(getEnabledCheck(lvtIndex, playerClassName));

		Set<String> targetMessages = Set.of(
			"perpendicular", "ascending_s_curve", "too_sharp", "slope_turn", "opposing_slopes",
			"leave_slope_ascending", "leave_slope_descending", "too_steep", "turn_90"
		);

		for(int i = 0; i < tryConnect.instructions.size(); i++) {
			AbstractInsnNode areturn = tryConnect.instructions.get(i);
			if(areturn.getOpcode() != ARETURN) continue;

			LdcInsnNode ldc = findPreviousNode(areturn, LdcInsnNode.class);
			if(ldc == null) continue;
			if(!(ldc.cst instanceof String message && targetMessages.contains(message))) continue;

			int diff = tryConnect.instructions.indexOf(areturn) - tryConnect.instructions.indexOf(ldc);
			if(diff != 2 && diff != 7) continue; // if just withMessage is called, will be 2, else if tooJumbly is called as well, will be 7

			JumpInsnNode jump = findPreviousNode(ldc, JumpInsnNode.class);
			if(jump == null) continue;
			LabelNode toInject = findPreviousNode(jump, LabelNode.class);
			tryConnect.instructions.insert(toInject, new JumpInsnNode(IFEQ, jump.label));
			tryConnect.instructions.insert(toInject, new VarInsnNode(ILOAD, lvtIndex));
			i += 2;
		}
	}

	private static InsnList getEnabledCheck(int lvtIndex, String playerClassName) {
		InsnList list = new InsnList();
		// get CUConfigs.server.placementChecks
		list.add(new FieldInsnNode(GETSTATIC, "dev/rdh/createunlimited/config/CUConfigs", "server", "Ldev/rdh/createunlimited/config/CUServer;"));
		list.add(new FieldInsnNode(GETFIELD, "dev/rdh/createunlimited/config/CUServer", "placementChecks", "Lcom/simibubi/create/foundation/config/ConfigBase$ConfigEnum;"));

		// get PlacementCheck.ON
		list.add(new FieldInsnNode(GETSTATIC, "dev/rdh/createunlimited/config/CUServer$PlacementCheck", "ON", "Ldev/rdh/createunlimited/config/CUServer$PlacementCheck;"));

		// call Util.orElse(CUConfigs.server.placementChecks, PlacementCheck.ON)
		list.add(new MethodInsnNode(INVOKESTATIC, "dev/rdh/createunlimited/Util", "orElse", "(Lcom/simibubi/create/foundation/config/ConfigBase$CValue;Ljava/lang/Object;)Ljava/lang/Object;"));

		// cast result of orElse to PlacementCheck
		list.add(new TypeInsnNode(CHECKCAST, "dev/rdh/createunlimited/config/CUServer$PlacementCheck"));

		// load player (second argument of tryConnect)
		list.add(new VarInsnNode(ALOAD, 1));

		// call isEnabledFor(player) on the PlacementCheck from above
		list.add(new MethodInsnNode(INVOKEVIRTUAL, "dev/rdh/createunlimited/config/CUServer$PlacementCheck", "isEnabledFor", "(" + playerClassName + ")Z"));

		// store result of isEnabledFor in local variable
		list.add(new VarInsnNode(ISTORE, lvtIndex));
		return list;
	}

	private static void dumpClass(ClassNode classNode) {
		ClassWriter writer = new ClassWriter(0);
		classNode.accept(writer);
		byte[] bytes = writer.toByteArray();
		Path path = Path.of(classNode.name + ".class");
		try {
			Files.write(path, bytes);
		} catch(Exception e) {
			throw new RuntimeException("Failed to write class file: " + path, e);
		}
	}

	private static <T extends AbstractInsnNode> T findPreviousNode(AbstractInsnNode node, Class<T> type) {
		while((node = node.getPrevious()) != null) {
			if(type.isInstance(node)) return type.cast(node);
		}
		return null;
	}
}
