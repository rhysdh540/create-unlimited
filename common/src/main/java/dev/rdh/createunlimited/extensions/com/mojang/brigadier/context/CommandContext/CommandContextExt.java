package dev.rdh.createunlimited.extensions.com.mojang.brigadier.context.CommandContext;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import manifold.rt.api.NoBootstrap;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import com.mojang.brigadier.context.CommandContext;

@Extension
@NoBootstrap
@SuppressWarnings("unused")
public class CommandContextExt {
	public static void message(@This CommandContext<CommandSourceStack> context, Component message) {
		#if PRE_CURRENT_MC_1_19_2
		context.getSource().sendSuccess(message, false);
		#elif POST_CURRENT_MC_1_20_1
		context.getSource().sendSuccess(() -> message, false);
		#endif
	}

	public static void message(@This CommandContext<CommandSourceStack> context, String message) {
		context.message(Component.nullToEmpty(message));
	}

	public static void error(@This CommandContext<CommandSourceStack> context, Component message) {
		context.getSource().sendFailure(message);
	}

	public static void error(@This CommandContext<CommandSourceStack> context, String message) {
		context.error(Component.nullToEmpty(message));
	}
}