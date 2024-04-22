package dev.rdh.createunlimited.asm;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import dev.rdh.createunlimited.Util;
import dev.rdh.createunlimited.config.CUConfigs;
import dev.rdh.createunlimited.config.CUServer;
import dev.rdh.createunlimited.config.CUServer.PlacementCheck;

import com.simibubi.create.foundation.config.ConfigBase.ConfigEnum;

import net.minecraft.world.entity.player.Player;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.objectweb.asm.Opcodes.*;

public final class Asm {

	public static void instrumentTrackPlacement(ClassNode targetClass) {
		if(!targetClass.name.equals("com/simibubi/create/content/trains/track/TrackPlacement")) {
			String caller = Thread.currentThread().stackTrace[2].className;
			throw new IllegalArgumentException("instrumentTrackPlacement called from \"" + caller + "\" with wrong target class: " + targetClass.name);
		}

		MethodNode tryConnect = targetClass.methods.stream()
			.filter(m -> m.name.equals("tryConnect"))
			.findFirst()
			.orElseThrow(() -> new NoSuchMethodError("Could not find tryConnect method in TrackPlacement"));

		int lvtIndex = injectEnabledCheck(tryConnect.instructions);

		Set<String> targetMessages = Set.of(
			"perpendicular", "ascending_s_curve", "too_sharp", "slope_turn", "opposing_slopes",
			"leave_slope_ascending", "leave_slope_descending", "too_steep", "turn_90"
		);

		for(int i = 0; i < tryConnect.instructions.size(); i++) {
			AbstractInsnNode areturn = tryConnect.instructions.get(i);
			if(areturn.opcode != ARETURN) continue;

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

	private static int injectEnabledCheck(InsnList list) {
		/*
		boolean [var0] = Util.orElse(CUConfigs.server.placementChecks, PlacementCheck.ON).isEnabledFor(player);
		compiles down to:

		GETSTATIC dev/rdh/createunlimited/config/CUConfigs.server : Ldev/rdh/createunlimited/config/CUServer;
		GETFIELD dev/rdh/createunlimited/config/CUServer.placementChecks : Lcom/simibubi/create/foundation/config/ConfigBase$ConfigEnum;
		GETSTATIC com/simibubi/create/foundation/utility/PlacementCheck.ON : Lcom/simibubi/create/foundation/utility/PlacementCheck;
		INVOKESTATIC dev/rdh/createunlimited/Util.orElse (Lcom/simibubi/create/foundation/config/ConfigBase$ConfigEnum;Ljava/lang/Object;)Ljava/lang/Object;
		CHECKCAST dev/rdh/createunlimited/config/CUServer$PlacementCheck;
		ALOAD 1
		INVOKEVIRTUAL dev/rdh/createunlimited/config/CUServer$PlacementCheck.isEnabledFor (Lnet/minecraft/world/entity/player/Player;)Z
		ISTORE [some free lvt index]
		 */

		int lvtIndex = list.size();
		InsnList toInject = new InsnList();
		// get CUConfigs.server.placementChecks
		toInject.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(CUConfigs.class), "server", Type.getDescriptor(CUServer.class)));
		toInject.add(new FieldInsnNode(GETFIELD, Type.getInternalName(CUServer.class), "placementChecks", Type.getDescriptor(ConfigEnum.class)));

		// get PlacementCheck.ON
		toInject.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(PlacementCheck.class), "ON", Type.getDescriptor(PlacementCheck.class)));

		// call Util.orElse(CUConfigs.server.placementChecks, PlacementCheck.ON)
		toInject.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(Util.class), "orElse", Type.getMethodDescriptor(Type.getType(Object.class), Type.getType(ConfigEnum.class), Type.getType(Object.class))));

		// cast result of orElse to PlacementCheck
		toInject.add(new TypeInsnNode(CHECKCAST, Type.getInternalName(PlacementCheck.class)));

		// load player (second argument of tryConnect)
		toInject.add(new VarInsnNode(ALOAD, 1));

		// call isEnabledFor(player) on the PlacementCheck from above
		toInject.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(PlacementCheck.class), "isEnabledFor", Type.getMethodDescriptor(Type.BOOLEAN_TYPE, Type.getType(Player.class))));

		// store result of isEnabledFor in local variable
		toInject.add(new VarInsnNode(ISTORE, lvtIndex));
		list.insert(toInject);
		return lvtIndex;
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
