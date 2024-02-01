package dev.rdh.createunlimited.command;

import com.mojang.brigadier.CommandDispatcher;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;


import dev.rdh.createunlimited.CreateUnlimited;

import com.simibubi.create.infrastructure.command.AllCommands;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.Collections;

public class CUCommands {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
		LiteralArgumentBuilder<CommandSourceStack> base = Commands.literal(CreateUnlimited.ID);
//			.then(CUConfigCommand.register()); - does not work in prod because obfuscation :(

		LiteralCommandNode<CommandSourceStack> root = dispatcher.register(base);

		CommandNode<CommandSourceStack> cu = dispatcher.findNode(Collections.singleton("cu"));
		if(cu != null) return;
		dispatcher.getRoot().addChild(AllCommands.buildRedirect("cu", root));
	}
}
