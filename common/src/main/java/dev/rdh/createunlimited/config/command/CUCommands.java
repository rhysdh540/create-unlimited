package dev.rdh.createunlimited.config.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import com.mojang.brigadier.context.CommandContext;

import com.simibubi.create.foundation.utility.Components;

import dev.rdh.createunlimited.util.Util;
import dev.rdh.createunlimited.CreateUnlimited;
import dev.rdh.createunlimited.config.CUConfig;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

import net.minecraftforge.common.ForgeConfigSpec;

import java.lang.reflect.Field;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.network.chat.Component.nullToEmpty;

public class CUCommands {
	/**
	 * Builds and registers the {@code /createunlimited} command, that changes configuration values.
	 * <p>
	 * Base command: {@code /createunlimited}
	 * <br>
	 * Subcommands:
	 * <ul>
	 *     <li>{@code /createunlimited <category> <config> get} - Gets the value of the config</li>
	 *     <li>{@code /createunlimited <category> <config> set <value>} - Sets the value of the config
	 *     <ul>
	 *         <li>Requires operator permissions on servers.</li>
	 *     </ul></li>
	 *     <li>{@code /createunlimited <category> <config> reset} - Resets the value of the config to its default value
	 *     <ul>
	 *         <li>Requires operator permissions on servers.</li>
	 *     </ul></li>
	 * </ul>
	 * This command uses Mojang's {@link com.mojang.brigadier Brigadier} library to parse arguments and send them to the server.
	 * @see <a href="https://github.com/Mojang/brigadier">Brigadier on GitHub</a>
	 */
	public static void registerConfigCommand() {
		List<MutableComponent> links = List.of(
			link("https://github.com/rhysdh540/create-unlimited", "GitHub", ChatFormatting.GRAY),
			link("https://modrinth.com/mod/create-unlimited", "Modrinth", ChatFormatting.GREEN),
			link("https://curseforge.com/minecraft/mc-mods/create-unlimited", "CurseForge", ChatFormatting.GOLD),
			link("https://discord.gg/2ubhDbMaZY", "Discord", ChatFormatting.BLUE)
		);

		LiteralArgumentBuilder<CommandSourceStack> base = literal(CreateUnlimited.ID).executes(context -> {
			message(CreateUnlimited.NAME + " v" + CreateUnlimited.VERSION + " by rdh\nVisit us on:", context);

			MutableComponent link = (MutableComponent) CommonComponents.EMPTY;
			links.forEach(a -> link.append(a).append(Component.literal(" ")));

			message(link, context);
			return Command.SINGLE_SUCCESS;
		});

		LiteralArgumentBuilder<CommandSourceStack> category = null;
		for (Field field : CUConfig.class.getDeclaredFields()) {
			//skip if not config value or string
			if (!ForgeConfigSpec.ConfigValue.class.isAssignableFrom(field.getType()) && field.getType() != String.class) continue;

			//change category if needed
			if (field.getType() == String.class) {
				if (category != null) base.then(category);
				category = literal(field.getName());

				//add description for category
				base.then(literal(field.getName()).executes(context -> {
					message(CUConfig.comments.get(field.getName()), context);
					return Command.SINGLE_SUCCESS;
				}));

				continue;
			}
			if(category == null)
				category = base; // if no category, append everything to base

			// get config as ConfigValue
			ForgeConfigSpec.ConfigValue<?> value;
			try {
				value = (ForgeConfigSpec.ConfigValue<?>) field.get(null);
			} catch (IllegalAccessException | ClassCastException e) {
				CreateUnlimited.LOGGER.error("Failed to get config value for " + field.getName(), e);
				continue;
			}

			//get, description, reset
			gdr(category, field, value);

			//set for boolean
			if (value instanceof ForgeConfigSpec.BooleanValue bValue)
				setBoolean(category, field, bValue);

				// set for enums
			else if (value.get() instanceof Enum<?>)
				setEnum(category, field, (ForgeConfigSpec.EnumValue<? extends Enum<?>>) value);

				// set for int
			else if (value instanceof ForgeConfigSpec.IntValue iValue)
				setInt(category, field, iValue);

				// set for double
			else if (value instanceof ForgeConfigSpec.DoubleValue dValue)
				setDouble(category, field, dValue);

		}
		if (category != null)
			base.then(category);
		Util.registerCommand(base);
	}

	private static boolean perms(CommandSourceStack source) {
		return source.hasPermission(4) || !source.getLevel().getServer().isDedicatedServer();
	}


	private static <T> void gdr(LiteralArgumentBuilder<CommandSourceStack> category, Field field, ForgeConfigSpec.ConfigValue<T> value) {
		category.then(literal(field.getName())
			.executes(context -> {
				message(field.getName() + ": " + CUConfig.comments.get(field.getName()), context);
				message("Current value: " + value.get(), context);
				message("Default value: " + value.getDefault(), context);
				return Command.SINGLE_SUCCESS;
			})
			.then(literal("reset").requires(CUCommands::perms)
				.executes(context -> {
					if(value.get().equals(value.getDefault())) {
						error("Value is already default!", context);
						return 0;
					}
					value.set(value.getDefault());
					message(field.getName() + " reset to: " + value.get(), context);
					return Command.SINGLE_SUCCESS;
				})
			)
		);
	}

	private static void setBoolean(LiteralArgumentBuilder<CommandSourceStack> category, Field field, ForgeConfigSpec.BooleanValue value) {
		category.then(literal(field.getName())
			.then(argument("value", BoolArgumentType.bool()).requires(CUCommands::perms)
				.executes(context -> {
					boolean set = BoolArgumentType.getBool(context, "value");
					value.set(set);
					message(field.getName() + " set to: " + set, context);
					return Command.SINGLE_SUCCESS;
				})
			)
		);
	}

	private static void setInt(LiteralArgumentBuilder<CommandSourceStack> category, Field field, ForgeConfigSpec.IntValue value) {
		category.then(literal(field.getName())
			.then(argument("value", IntegerArgumentType.integer()).requires(CUCommands::perms)
				.executes(context -> {
					int set = IntegerArgumentType.getInteger(context, "value");
					value.set(set);
					message(field.getName() + " set to: " + set, context);
					return Command.SINGLE_SUCCESS;
				})
			)
		);
	}

	private static void setDouble(LiteralArgumentBuilder<CommandSourceStack> category, Field field, ForgeConfigSpec.DoubleValue value) {
		category.then(literal(field.getName())
			.then(argument("value", DoubleArgumentType.doubleArg()).requires(CUCommands::perms)
				.executes(context -> {
					double set = DoubleArgumentType.getDouble(context, "value");
					value.set(set);
					message(field.getName() + " set to: " + set, context);
					return Command.SINGLE_SUCCESS;
				})
			)
		);
	}

	private static <T extends Enum<T>> void setEnum(LiteralArgumentBuilder<CommandSourceStack> category, Field field, ForgeConfigSpec.EnumValue<T> value) {
		category.then(literal(field.getName())
			.then(argument("value", EnumArgument.enumArg(value.get().getClass(), true))
				.executes(context -> {
					T set = (T) context.getArgument("value", value.get().getClass());
					value.set(set);
					message(field.getName() + " set to: " + set.name().toLowerCase(), context);
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

	private static void message(String message, CommandContext<CommandSourceStack> context) {
		message(nullToEmpty(message), context);
	}
	private static void message(Component message, CommandContext<CommandSourceStack> context) {
		#if PRE_CURRENT_MC_1_19_2
			context.getSource().sendSuccess(message, false);
		#elif POST_CURRENT_MC_1_20_1
			context.getSource().sendSuccess(() -> message, false);
		#else
			#error "Unsupported Minecraft version"
		#endif
	}
	private static void error(String message, CommandContext<CommandSourceStack> context) {
		context.getSource().sendFailure(nullToEmpty(message));
	}
}