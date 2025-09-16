package dev.rdh.createunlimited.asm;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import dev.rdh.createunlimited.config.CUConfig;
import dev.rdh.createunlimited.config.PlacementCheck;

import net.createmod.catnip.config.ConfigBase.CValue;
import net.createmod.catnip.config.ConfigBase.ConfigEnum;

import org.objectweb.asm.util.*;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;


public final class Asm implements Opcodes {

	public static void instrumentTrackPlacement(ClassNode targetClass) {
		if(!targetClass.name.equals("com/simibubi/create/content/trains/track/TrackPlacement")) {
			String caller = Thread.currentThread().getStackTrace()[2].getClassName();
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

			GETSTATIC dev/rdh/createunlimited/config/CUConfig.instance : Ldev/rdh/createunlimited/config/CUConfig;
			GETFIELD dev/rdh/createunlimited/config/CUConfig.placementChecks : Lnet/createmod/catnip/config/ConfigBase$ConfigEnum;
			GETSTATIC dev/rdh/createunlimited/config/PlacementCheck.ON : Ldev/rdh/createunlimited/config/PlacementCheck;
			INVOKESTATIC dev/rdh/createunlimited/config/CUConfig.getOrDefault (Lnet/createmod/catnip/config/ConfigBase$CValue;Ljava/lang/Object;)Ljava/lang/Object;
			CHECKCAST dev/rdh/createunlimited/config/PlacementCheck; // because generics
			ALOAD 1
			INVOKEVIRTUAL dev/rdh/createunlimited/config/PlacementCheck.isEnabledFor (Lnet/minecraft/world/entity/player/Player;)Z
			ISTORE [lvtIndex]
		 	*/

			AbstractInsnNode[] toInject = new AbstractInsnNode[] {
				// get CUConfigs.server.placementChecks
				new FieldInsnNode(GETSTATIC, Type.getInternalName(CUConfig.class), "instance", Type.getDescriptor(CUConfig.class)),
				new FieldInsnNode(GETFIELD, Type.getInternalName(CUConfig.class), "placementChecks", Type.getDescriptor(ConfigEnum.class)),

				// get PlacementCheck.ON
				new FieldInsnNode(GETSTATIC, Type.getInternalName(PlacementCheck.class), "ON", Type.getDescriptor(PlacementCheck.class)),

				// call CUConfigs.getOrDefault(CUConfigs.server.placementChecks, PlacementCheck.ON)
				new MethodInsnNode(INVOKESTATIC, Type.getInternalName(CUConfig.class), "getOrDefault", Type.getMethodDescriptor(Type.getType(Object.class), Type.getType(CValue.class), Type.getType(Object.class))),

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
			if(areturn.getOpcode() != ARETURN) continue;

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

	// com.copycatsplus.copycats.foundation.copycat.ICopycatBlock
	public static void instrumentICopycatBlock(ClassNode targetClass) {
		if (!targetClass.name.equals("com/copycatsplus/copycats/foundation/copycat/ICopycatBlock")) {
			String caller = Thread.currentThread().getStackTrace()[2].getClassName();
			throw new IllegalArgumentException("instrumentICopycatBlock called from \"" + caller + "\" with wrong target class: " + targetClass.name);
		}

		// this is an interface, we want to modify a default method:
		// default BlockState getAcceptedBlockState(Level, BlockPos, ItemStack, Direction)
		MethodNode method = targetClass.methods.stream()
			.filter(m -> m.name.equals("getAcceptedBlockState")
				&& m.desc.equals(Type.getMethodDescriptor(
					Type.getType(BlockState.class),
					Type.getType(Level.class),
					Type.getType(BlockPos.class),
					Type.getType(ItemStack.class),
					Type.getType(Direction.class)
			)))
			.findFirst()
			.orElseThrow(() -> new NoSuchMethodError("Could not find getAcceptedBlockState method in ICopycatBlock"));

		// modify call to this.isAcceptedRegardless(BlockState)Z, add:
		//   || CUConfig.getOrDefault(CUConfig.instance.allowAllCopycatBlocks, false)
		for(int i = 0; i < method.instructions.size(); i++) {
			AbstractInsnNode insn = method.instructions.get(i);
			if(insn.getOpcode() != INVOKEINTERFACE) continue;
			MethodInsnNode m = (MethodInsnNode)insn;
			if(!m.name.equals("isAcceptedRegardless")) continue;
			if(!m.desc.equals(Type.getMethodDescriptor(Type.BOOLEAN_TYPE, Type.getType(BlockState.class)))) continue;
			if(!m.owner.equals(targetClass.name)) continue;
			if(!m.itf) continue;

			// found the call we want to modify
			/* compiles down to:
			GETSTATIC dev/rdh/createunlimited/config/CUConfig.instance : Ldev/rdh/createunlimited/config/CUConfig;
			GETFIELD dev/rdh/createunlimited/config/CUConfig.allowAllCopycatBlocks : Lnet/createmod/catnip/config/ConfigBase$CValue;
			ICONST_0
			INVOKESTATIC dev/rdh/createunlimited/config/CUConfig.getOrDefault (Lnet/createmod/catnip/config/ConfigBase$CValue;Ljava/lang/Object;)Ljava/lang/Object;
			CHECKCAST java/lang/Boolean
			INVOKEVIRTUAL java/lang/Boolean.booleanValue ()Z
			IOR
			*/
			AbstractInsnNode[] toInject = new AbstractInsnNode[] {
				// get CUConfig.instance
				new FieldInsnNode(GETSTATIC, Type.getInternalName(CUConfig.class), "instance", Type.getDescriptor(CUConfig.class)),
				// get CUConfig.instance.allowAllCopycatBlocks
				new FieldInsnNode(GETFIELD, Type.getInternalName(CUConfig.class), "allowAllCopycatBlocks", /*Type.getDescriptor(ConfigBool.class)*/ "Lnet/createmod/catnip/config/ConfigBase$ConfigBool;"),
				new TypeInsnNode(CHECKCAST,/*Type.getInternalName(CValue.class)*/ "net/createmod/catnip/config/ConfigBase$CValue"),
				// push Boolean.FALSE onto stack
				new FieldInsnNode(GETSTATIC, Type.getInternalName(Boolean.class), "FALSE", Type.getDescriptor(Boolean.class)),
				// call CUConfig.getOrDefault(allowAllCopycatBlocks, false)
				new MethodInsnNode(INVOKESTATIC, Type.getInternalName(CUConfig.class), "getOrDefault", Type.getMethodDescriptor(
					Type.getType(Object.class), Type.getType(/*CValue.class*/"Lnet/createmod/catnip/config/ConfigBase$CValue;"), Type.getType(Object.class)
				)),
				// cast result to Boolean
				new TypeInsnNode(CHECKCAST, Type.getInternalName(Boolean.class)),
				// call booleanValue() on the Boolean
				new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(Boolean.class), "booleanValue", Type.getMethodDescriptor(Type.BOOLEAN_TYPE)),
				// OR the result of booleanValue() with the result of isAcceptedRegardless()
				new InsnNode(IOR),
			};

			for (int j = toInject.length - 1; j >= 0; j--) {
				method.instructions.insert(insn, toInject[j]);
			}
			i += toInject.length;
		}

		dumpClass(targetClass);
	}

	private static void dumpClass(ClassNode classNode) {
		ClassWriter writer = new ClassWriter(0);
		classNode.accept(writer);
		byte[] bytes = writer.toByteArray();

		// just have name.class, not package/name.class
		int i = classNode.name.lastIndexOf('/');
		Path path = Path.of(classNode.name.substring(i + 1) + ".class");
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
