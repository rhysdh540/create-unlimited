package dev.rdh.createunlimited.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.rdh.createunlimited.CreateUnlimited;
import dev.rdh.createunlimited.config.CUConfig;
import dev.rdh.createunlimited.asm.mixin.accessor.CValueAccessor;

import com.simibubi.create.foundation.config.ConfigBase.CValue;
import com.simibubi.create.foundation.config.ConfigBase.ConfigGroup;

import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

import net.minecraft.commands.CommandSourceStack;

import java.lang.reflect.Field;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class CUConfigCommand extends CUCommands {
	private final boolean integrated;

	public CUConfigCommand(boolean integrated) {
		this.integrated = integrated;
	}

	@Override
	public ArgumentBuilder<CommandSourceStack, ?> register() {
		LiteralArgumentBuilder<CommandSourceStack> base = literal("config");

		LiteralArgumentBuilder<CommandSourceStack> category = null;

		for (Field field : CUConfig.class.getDeclaredFields()) {
			// skip if not config value
			if (!CValue.class.isAssignableFrom(field.getType())) continue;

			final String name = field.getName();

			// change category if needed
			if (field.getType() == ConfigGroup.class) {
				if (category != null) base.then(category);
				category = literal(name);

				// add description for category
				base.then(literal(name).executes(context -> {
					message(context, CUConfig.getComment(name));
					return Command.SINGLE_SUCCESS;
				}));

				continue;
			}
			if(category == null) category = base;

			// get config as CValue
			CValue<?, ?> cValue;
			try {
				cValue = (CValue<?, ?>) field.get(CUConfig.instance);
			} catch (IllegalAccessException | ClassCastException e) {
				//noinspection StringConcatenationArgumentToLogCall
				CreateUnlimited.LOGGER.error("Failed to get config value for " + name, e);
				continue;
			}

			configure(category, name, ((CValueAccessor) cValue).getValue());
		}

		if (category != null)
			base.then(category);
		return base;
	}

	private boolean perms(CommandSourceStack source) {
		return integrated || source.hasPermission(2);
	}

	private <T> void configure(LiteralArgumentBuilder<CommandSourceStack> category, String name, ConfigValue<T> value) {
		@SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>) value.getDefault().getClass();

		category.then(literal(name)
			.executes(context -> {
				message(context, name + ": " + CUConfig.getComment(name));
				message(context, "Current value: " + value.get());
				message(context, "Default value: " + value.getDefault());
				return Command.SINGLE_SUCCESS;
			})
			.then(literal("reset").requires(this::perms)
				.executes(context -> {
					if(value.get().equals(value.getDefault())) {
						error(context, "Value is already default!");
						return 0;
					}
					value.set(value.getDefault());
					message(context, name + " reset to: " + value.get());
					return Command.SINGLE_SUCCESS;
				})
			)
			.then(argument("value", getArgument(value)).requires(this::perms)
				.executes(context -> {
					T set = context.getArgument("value", clazz);
					if(set == value.get()) {
						error(context, "Value is already set to " + set);
						return 0;
					}
					value.set(set);
					message(context, "Value set to: " + set);
					return Command.SINGLE_SUCCESS;
				})
			)
		);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private <T> ArgumentType<T> getArgument(ConfigValue<T> value) {
		if(value instanceof BooleanValue) return (ArgumentType<T>) BoolArgumentType.bool();
		if(value instanceof DoubleValue) return (ArgumentType<T>) DoubleArgumentType.doubleArg();
		if(value instanceof IntValue) return (ArgumentType<T>) IntegerArgumentType.integer();
		Class<T> clazz = (Class<T>) value.getDefault().getClass();
		if(value instanceof EnumValue<?> && clazz.isEnum()) return (ArgumentType<T>) EnumArgument.enumArg((Class<Enum>) clazz, true);
		throw new IllegalArgumentException("Unsupported class for argument: " + clazz);
	}
}
