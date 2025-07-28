package dev.rdh.createunlimited.command;

import com.mojang.brigadier.CommandDispatcher;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import dev.rdh.createunlimited.CreateUnlimited;

import net.createmod.catnip.command.CatnipCommands;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

import java.util.Collections;
import java.util.List;

public abstract class CUCommands {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, CommandSelection environment) {
		List<MutableComponent> links = List.of(
			link("https://github.com/rhysdh540/create-unlimited", "GitHub", ChatFormatting.GRAY),
			link("https://modrinth.com/mod/create-unlimited", "Modrinth", ChatFormatting.GREEN),
			link("https://curseforge.com/minecraft/mc-mods/create-unlimited", "CurseForge", ChatFormatting.GOLD),
			link("https://discord.gg/GeGm3DRDWY", "Discord", ChatFormatting.BLUE)
		);

		boolean includeIntegrated = environment == CommandSelection.ALL || environment == CommandSelection.INTEGRATED;

		CUCommands[] commands = {
			new CUConfigCommand(includeIntegrated),
		};

		LiteralArgumentBuilder<CommandSourceStack> base = Commands.literal(CreateUnlimited.ID)
			.executes(context -> {
				message(context, CreateUnlimited.NAME + " v" + CreateUnlimited.VERSION + " by rdh\nVisit us on:");
				MutableComponent link = Component.empty();
				links.forEach(a -> link.append(a).append(Component.literal(" ")));
				message(context, link);
				return 1;
			});

		for (CUCommands command : commands) {
			base.then(command.register());
		}

		LiteralCommandNode<CommandSourceStack> root = dispatcher.register(base);

		CommandNode<CommandSourceStack> cu = dispatcher.findNode(Collections.singleton("cu"));
		if(cu != null) return;
		dispatcher.getRoot().addChild(CatnipCommands.buildRedirect("cu", root));
	}

	protected static MutableComponent link(String link, String display, ChatFormatting color) {
		return ComponentUtils.wrapInSquareBrackets(Component.nullToEmpty(display))
			.withStyle(color)
			.withStyle(style -> style
				.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link))
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to open " + display + " page")))
				.withUnderlined(false));
	}

	protected static void message(CommandContext<CommandSourceStack> context, Component message) {
		context.getSource().sendSystemMessage(message);
	}

	protected static void message(CommandContext<CommandSourceStack> context, String message) {
		message(context, Component.nullToEmpty(message));
	}

	protected static void error(CommandContext<CommandSourceStack> context, Component message) {
		context.getSource().sendFailure(message);
	}

	protected static void error(CommandContext<CommandSourceStack> context, String message) {
		error(context, Component.nullToEmpty(message));
	}

	public abstract ArgumentBuilder<CommandSourceStack, ?> register();
}
