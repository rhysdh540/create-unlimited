package dev.rdh.createunlimited.asm;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import dev.rdh.createunlimited.Util;
import dev.rdh.createunlimited.config.CUConfigs;
import dev.rdh.createunlimited.config.CUServer;
import dev.rdh.createunlimited.config.PlacementCheck;

import com.simibubi.create.foundation.config.ConfigBase.CValue;
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
		int lvtIndex = tryConnect.localVariables.size();

		{ // inject the thing to see if we should check for limits or not
			/*
			boolean [var0] = CUConfigs.getOrDefault(CUConfigs.server.placementChecks, PlacementCheck.ON).isEnabledFor(player);
			compiles down to:

			GETSTATIC dev/rdh/createunlimited/config/CUConfigs.server : Ldev/rdh/createunlimited/config/CUServer;
			GETFIELD dev/rdh/createunlimited/config/CUServer.placementChecks : Lcom/simibubi/create/foundation/config/ConfigBase$ConfigEnum;
			GETSTATIC com/simibubi/create/foundation/utility/PlacementCheck.ON : Lcom/simibubi/create/foundation/utility/PlacementCheck;
			INVOKESTATIC dev/rdh/createunlimited/config/CUConfigs.getOrDefault (Lcom/simibubi/create/foundation/config/ConfigBase$CValue;Ljava/lang/Object;)Ljava/lang/Object;
			CHECKCAST dev/rdh/createunlimited/config/PlacementCheck; // because generics
			ALOAD 1
			INVOKEVIRTUAL dev/rdh/createunlimited/config/PlacementCheck.isEnabledFor (Lnet/minecraft/world/entity/player/Player;)Z
			ISTORE [lvtIndex]
		 	*/

			AbstractInsnNode[] toInject = new AbstractInsnNode[] {
				// get CUConfigs.server.placementChecks
				new FieldInsnNode(GETSTATIC, Type.getInternalName(CUConfigs.class), "server", Type.getDescriptor(CUServer.class)),
				new FieldInsnNode(GETFIELD, Type.getInternalName(CUServer.class), "placementChecks", Type.getDescriptor(ConfigEnum.class)),

				// get PlacementCheck.ON
				new FieldInsnNode(GETSTATIC, Type.getInternalName(PlacementCheck.class), "ON", Type.getDescriptor(PlacementCheck.class)),

				// call CUConfigs.getOrDefault(CUConfigs.server.placementChecks, PlacementCheck.ON)
				new MethodInsnNode(INVOKESTATIC, Type.getInternalName(CUConfigs.class), "getOrDefault", Type.getMethodDescriptor(Type.getType(Object.class), Type.getType(CValue.class), Type.getType(Object.class))),

				// cast result of orElse to PlacementCheck
				new TypeInsnNode(CHECKCAST, Type.getInternalName(PlacementCheck.class)),

				// load player (second argument of tryConnect)
				new VarInsnNode(ALOAD, 1),

				// call isEnabledFor(player) on the PlacementCheck from above
				new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(PlacementCheck.class), "isEnabledFor", Type.getMethodDescriptor(Type.BOOLEAN_TYPE, Type.getType(Player.class))),

				// store result of isEnabledFor in local variable
				new VarInsnNode(ISTORE, lvtIndex),
			};

			for(int i = toInject.length - 1; i >= 0; i--) {
				tryConnect.instructions.insert(toInject[i]);
			}
		}

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
			if(diff != 2 && diff != 7)
				continue; // if just withMessage is called, will be 2, else if tooJumbly is called as well, will be 7

			JumpInsnNode jump = findPreviousNode(ldc, JumpInsnNode.class);
			if(jump == null) continue;
			LabelNode toInject = findPreviousNode(jump, LabelNode.class);
			tryConnect.instructions.insert(toInject, new JumpInsnNode(IFEQ, jump.label));
			tryConnect.instructions.insert(toInject, new VarInsnNode(ILOAD, lvtIndex));
			i += 2;
		}
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
