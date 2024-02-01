package dev.rdh.createunlimited.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import dev.rdh.createunlimited.CreateUnlimited;
import dev.rdh.createunlimited.Util;
import dev.rdh.createunlimited.config.CUConfigs;
import dev.rdh.createunlimited.config.CUServer;
import dev.rdh.createunlimited.mixin.accessor.CValueAccessor;

import com.simibubi.create.foundation.config.ConfigBase.CValue;
import com.simibubi.create.foundation.config.ConfigBase.ConfigGroup;
import com.simibubi.create.foundation.utility.Components;

import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Supplier;

import static dev.rdh.createunlimited.multiversion.SupportedMinecraftVersion.*;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class CUConfigCommand {
	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		List<MutableComponent> links = List.of(
			link("https://github.com/rhysdh540/create-unlimited", "GitHub", ChatFormatting.GRAY),
			link("https://modrinth.com/mod/create-unlimited", "Modrinth", ChatFormatting.GREEN),
			link("https://curseforge.com/minecraft/mc-mods/create-unlimited", "CurseForge", ChatFormatting.GOLD),
			link("https://discord.gg/GeGm3DRDWY", "Discord", ChatFormatting.BLUE)
		);
		LiteralArgumentBuilder<CommandSourceStack> base = literal("config")
			.executes(context -> {
				sendSuccess(context, CreateUnlimited.NAME + " v" + CreateUnlimited.VERSION + " by rdh\nVisit us on:");
				MutableComponent link = MutableComponent.create(CommonComponents.EMPTY.getContents());
				links.forEach(a -> link.append(a).append(Component.literal(" ")));
				sendSuccess(context, link);
				return 1;
			});

		LiteralArgumentBuilder<CommandSourceStack> category = null;

		for (Field field : CUServer.class.getDeclaredFields()) {
			// skip if not config value or string
			if (!CValue.class.isAssignableFrom(field.getType())) continue;

			String name = field.getName();

			// change category if needed
			if (field.getType() == ConfigGroup.class) {
				if (category != null) base.then(category);
				category = literal(name);

				// add description for category
				assert base != null;
				base.then(literal(field.getName()).executes(context -> {
					sendSuccess(context, CUServer.getComment(name));
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

			// get config as forge config value
			ConfigValue<?> value = ((CValueAccessor) cValue).value;

			// get, description, reset
			gdr(category, name, value);

			// set for boolean
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

		if (category != null)
			base.then(category);
		return base;
	}

	private static boolean perms(Object o) {
		if(!(o instanceof CommandSourceStack source)) return false;
		Entity e = source.getEntity();
		return e != null && e.hasPermissions(4);
	}

	private static <T> void gdr(LiteralArgumentBuilder<CommandSourceStack> category, String name, ConfigValue<T> value) {
		category.then(literal(name)
			.executes(context -> {
				sendSuccess(context, name + ": " + CUServer.getComment(name));
				sendSuccess(context, "Current value: " + value.get());
				sendSuccess(context, "Default value: " + value.getDefault());
				return Command.SINGLE_SUCCESS;
			})
			.then(literal("reset").requires(CUConfigCommand::perms)
				.executes(context -> {
					if(value.get().equals(value.getDefault())) {
						error(context, "Value is already default!");
						return 0;
					}
					value.set(value.getDefault());
					sendSuccess(context, name + " reset to: " + value.get());
					return Command.SINGLE_SUCCESS;
				})
			)
		);
	}

	private static void setBoolean(LiteralArgumentBuilder<CommandSourceStack> category, String name, BooleanValue value) {
		category.then(literal(name)
			.then(argument("value", BoolArgumentType.bool()).requires(CUConfigCommand::perms)
				.executes(context -> {
					boolean set = BoolArgumentType.getBool(context, "value");
					if(set == value.get()) {
						error(context, "Value is already set to " + set);
						return 0;
					}
					value.set(set);
					sendSuccess(context, name + " set to: " + set);
					return Command.SINGLE_SUCCESS;
				})
			)
		);
	}

	private static void setInt(LiteralArgumentBuilder<CommandSourceStack> category, String name, IntValue value) {
		category.then(literal(name)
			.then(argument("value", IntegerArgumentType.integer()).requires(CUConfigCommand::perms)
				.executes(context -> {
					int set = IntegerArgumentType.getInteger(context, "value");
					if(set == value.get()) {
						error(context, "Value is already set to " + set);
						return 0;
					}
					value.set(set);
					sendSuccess(context, name + " set to: " + set);
					return Command.SINGLE_SUCCESS;
				})
			)
		);
	}

	private static void setDouble(LiteralArgumentBuilder<CommandSourceStack> category, String name, DoubleValue value) {
		category.then(literal(name)
			.then(argument("value", DoubleArgumentType.doubleArg()).requires(CUConfigCommand::perms)
				.executes(context -> {
					double set = DoubleArgumentType.getDouble(context, "value");
					if(set == value.get()) {
						error(context, "Value is already set to " + set);
						return 0;
					}
					value.set(set);
					sendSuccess(context, name + " set to: " + set);
					return Command.SINGLE_SUCCESS;
				})
			)
		);
	}

	@SuppressWarnings("unchecked")
	private static <T extends Enum<T>> void setEnum(LiteralArgumentBuilder<CommandSourceStack> category, String name, EnumValue<T> value) {
		category.then(literal(name)
			.then(argument("value", EnumArgument.enumArg(value.getDefault().getClass(), true))
				.requires(CUConfigCommand::perms)
				.executes(context -> {
					T set = EnumArgument.getEnum(context, "value", (Class<T>) value.getDefault().getClass());
					if(set == value.get()) {
						error(context, "Value is already set to " + set.name().toLowerCase());
						return 0;
					}
					value.set(set);
					sendSuccess(context, name + " set to: " + set.name().toLowerCase());
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

	private static final MethodHandle SEND_SUCCESS;
	static  {
		MethodHandles.Lookup lookup = MethodHandles.publicLookup();
		MethodType type;
		if(CURRENT <= v1_19_2) {
			type = MethodType.methodType(void.class, Component.class, boolean.class);
		} else if(CURRENT >= v1_20_1) {
			type = MethodType.methodType(void.class, Supplier.class, boolean.class);
		} else {
			throw new RuntimeException("Unsupported Minecraft version: " + CURRENT);
		}

		String remapped = Util.remapMethod(CommandSourceStack.class, "sendSuccess", type.parameterArray());
		try {
			SEND_SUCCESS = lookup.findVirtual(CommandSourceStack.class, remapped, type);
		} catch(NoSuchMethodException | IllegalAccessException e) {
			throw unchecked(e);
		}
	}

	private static void sendSuccess(CommandContext<CommandSourceStack> context, Component message) {
		try {
			if(CURRENT <= v1_19_2)
				SEND_SUCCESS.invokeExact(context.getSource(), message, false);
			else if(CURRENT >= v1_20_1)
				SEND_SUCCESS.invoke(context.getSource(), (Supplier<Component>) () -> message, false);
			else
				throw new RuntimeException("Unsupported Minecraft version: " + CURRENT);
		} catch(Throwable t) {
			throw unchecked(t);
		}
	}

	private static void sendSuccess(CommandContext<CommandSourceStack> context, String message) {
		sendSuccess(context, Component.nullToEmpty(message));
	}

	private static void error(CommandContext<CommandSourceStack> context, Component message) {
		context.getSource().sendFailure(message);
	}

	private static void error(CommandContext<CommandSourceStack> context, String message) {
		error(context, Component.nullToEmpty(message));
	}
}
