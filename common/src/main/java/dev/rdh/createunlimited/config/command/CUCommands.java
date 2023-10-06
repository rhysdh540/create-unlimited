package dev.rdh.createunlimited.config.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import com.mojang.brigadier.context.CommandContext;

import com.simibubi.create.foundation.config.ConfigBase.*;
import com.simibubi.create.foundation.utility.Components;

import dev.rdh.createunlimited.Util;
import dev.rdh.createunlimited.CreateUnlimited;
import dev.rdh.createunlimited.config.CUConfigs;

import dev.rdh.createunlimited.config.CUServer;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

import net.minecraftforge.common.ForgeConfigSpec.*;

import java.util.List;

import manifold.ext.rt.extensions.java.lang.Object.ManObjectExt;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.network.chat.Component.nullToEmpty;

public class CUCommands {
	public static void registerConfigCommand() {
		List<MutableComponent> links = List.of(
			link("https://github.com/rhysdh540/create-unlimited", "GitHub", ChatFormatting.GRAY),
			link("https://modrinth.com/mod/create-unlimited", "Modrinth", ChatFormatting.GREEN),
			link("https://curseforge.com/minecraft/mc-mods/create-unlimited", "CurseForge", ChatFormatting.GOLD),
			link("https://discord.gg/2ubhDbMaZY", "Discord", ChatFormatting.BLUE)
		);

		LiteralArgumentBuilder<CommandSourceStack> base = literal(CreateUnlimited.ID).executes(context -> {
			context.message(CreateUnlimited.NAME + " v" + CreateUnlimited.VERSION + " by rdh\nVisit us on:");

			var link = MutableComponent.create(CommonComponents.EMPTY.getContents());
			links.forEach(a -> link.append(a).append(Component.literal(" ")));

			context.message(link);
			return Command.SINGLE_SUCCESS;
		});

		LiteralArgumentBuilder<CommandSourceStack> category = null;

		for (var field : CUServer.class.getDeclaredFields()) {
			//skip if not config value or string
			if (!CValue.class.isAssignableFrom(field.getType())) continue;

			String name = field.getName();

			//change category if needed
			if (field.getType() == ConfigGroup.class) {
				if (category != null) base.then(category);
				category = literal(name);

				//add description for category
				assert base != null;
				base.then(literal(field.getName()).executes(context -> {
					context.message(CUServer.getComment(name));
					return Command.SINGLE_SUCCESS;
				}));

				continue;
			}
			if(category == null) category = base;

			// get config as CValue
			CValue<?, ?> cValue;
			try {
				cValue = (CValue<?, ?>) field.get(CUConfigs.server());
			} catch (IllegalAccessException | ClassCastException e) {
				CreateUnlimited.LOGGER.error("Failed to get config value for " + field.getName(), e);
				continue;
			}

			ConfigValue<?> value = cValue.jailbreak().value;

			//get, description, reset
			gdr(category, name, value);

			//set for boolean
			if (value instanceof BooleanValue bValue)
				setBoolean(category, name, bValue);

			// set for enums
			else if (value instanceof EnumValue<? extends Enum<?>> eValue)
				setEnum(category, name, eValue);

			// set for int
			else if (value instanceof IntValue iValue)
				setInt(category, name, iValue);

			// set for double
			else if (value instanceof DoubleValue dValue)
				setDouble(category, name, dValue);

		}

		if(Util.isDevEnv()) {
			base.then(literal("disableEverything").requires(CUCommands::perms).executes(context -> {
				CUConfigs.server().placementChecks.set(CUServer.PlacementCheck.OFF);
				CUConfigs.server().extendedDriving.set(0.01);
				CUConfigs.server().maxTrainRelocationDistance.set(128d);
				CUConfigs.server().maxAllowedStress.set(-1d);
				CUConfigs.server().trainAssemblyChecks.set(false);
				CUConfigs.server().maxGlueConnectionRange.set(128d);
				//CUConfigs.server().physicalBlockConnection.set(false);
				CUConfigs.server().singleExtendoGripRange.set(128);
				CUConfigs.server().doubleExtendoGripRange.set(128);
				CUConfigs.server().allowAllCopycatBlocks.set(true);
				CUConfigs.server().singleExtendoGripRange.set(128);
				CUConfigs.server().doubleExtendoGripRange.set(128);
				return Command.SINGLE_SUCCESS;
			}));
		}

		if (category != null)
			base.then(category);
		Util.registerCommand(base);
	}

	private static boolean perms(Object o) {
		return o instanceof CommandSourceStack source && (source.hasPermission(4) || !source.getLevel().getServer().isDedicatedServer());
	}

	private static <T> void gdr(LiteralArgumentBuilder<CommandSourceStack> category, String name, ConfigValue<T> value) {
		category.then(literal(name)
			.executes(context -> {
				context.message(name + ": " + CUServer.getComment(name));
				context.message("Current value: " + value.get());
				context.message("Default value: " + value.getDefault());
				return Command.SINGLE_SUCCESS;
			})
			.then(literal("reset").requires(CUCommands::perms)
				.executes(context -> {
					if(value.get().equals(value.getDefault())) {
						context.error("Value is already default!");
						return 0;
					}
					value.set(value.getDefault());
					context.message(name + " reset to: " + value.get());
					return Command.SINGLE_SUCCESS;
				})
			)
		);
	}

	private static void setBoolean(LiteralArgumentBuilder<CommandSourceStack> category, String name, BooleanValue value) {
		category.then(literal(name)
			.then(argument("value", BoolArgumentType.bool()).requires(CUCommands::perms)
				.executes(context -> {
					var set = BoolArgumentType.getBool(context, "value");
					if(set == value.get()) {
						context.error("Value is already set to " + set);
						return 0;
					}
					value.set(set);
					context.message(name + " set to: " + set);
					return Command.SINGLE_SUCCESS;
				})
			)
		);
	}

	private static void setInt(LiteralArgumentBuilder<CommandSourceStack> category, String name, IntValue value) {
		category.then(literal(name)
			.then(argument("value", IntegerArgumentType.integer()).requires(CUCommands::perms)
				.executes(context -> {
					var set = IntegerArgumentType.getInteger(context, "value");
					if(set == value.get()) {
						context.error("Value is already set to " + set);
						return 0;
					}
					value.set(set);
					context.message(name + " set to: " + set);
					return Command.SINGLE_SUCCESS;
				})
			)
		);
	}

	private static void setDouble(LiteralArgumentBuilder<CommandSourceStack> category, String name, DoubleValue value) {
		category.then(literal(name)
			.then(argument("value", DoubleArgumentType.doubleArg()).requires(CUCommands::perms)
				.executes(context -> {
					var set = DoubleArgumentType.getDouble(context, "value");
					if(set == value.get()) {
						context.error("Value is already set to " + set);
						return 0;
					}
					value.set(set);
					context.message(name + " set to: " + set);
					return Command.SINGLE_SUCCESS;
				})
			)
		);
	}

	@SuppressWarnings("unchecked")
	private static <T extends Enum<T>> void setEnum(LiteralArgumentBuilder<CommandSourceStack> category, String name, EnumValue<T> value) {
		category.then(literal(name)
			.then(argument("value", EnumArgument.enumArg(value.getDefault().getClass(), true)).requires(CUCommands::perms)
				.executes(context -> {
					var set = (T) context.getArgument("value", value.get().getClass());
					if(set == value.get()) {
						context.error("Value is already set to " + set.name().toLowerCase());
						return 0;
					}
					value.set(set);
					context.message(name + " set to: " + set.name().toLowerCase());
					return Command.SINGLE_SUCCESS;
				})
			)
		);
	}

	private static MutableComponent link(String link, String display, ChatFormatting color) {
		return ComponentUtils.wrapInSquareBrackets(Component.nullToEmpty(display))
			.withStyle(color)
			.withStyle(style -> style
				.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link))
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Components.literal("Click to open " + display + " page")))
				.withUnderlined(false));
	}
}