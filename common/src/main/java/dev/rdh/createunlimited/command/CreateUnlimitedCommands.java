package dev.rdh.createunlimited.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import com.mojang.brigadier.context.CommandContext;

import com.simibubi.create.foundation.utility.Components;

import dev.rdh.createunlimited.CUPlatformFunctions;
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

import net.minecraftforge.server.command.EnumArgument;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.network.chat.Component.nullToEmpty;

public class CreateUnlimitedCommands {
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

        LiteralArgumentBuilder<CommandSourceStack> base = literal("createunlimited").executes(context -> {
			message("Create Unlimited v" + CreateUnlimited.VERSION + " by rdh\nVisit us on:", context);

			MutableComponent link = (MutableComponent) CommonComponents.EMPTY;
			links.forEach(a -> link.append(a).append(Component.literal(" ")));

			context.getSource().sendSuccess(link, false);
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
            assert category != null;

            // get config as ConfigValue
            ForgeConfigSpec.ConfigValue<?> value;
            try {
                value = (ForgeConfigSpec.ConfigValue<?>) field.get(null);
            } catch (IllegalAccessException | ClassCastException e) {
                CreateUnlimited.LOGGER.error("Failed to get config value for " + field.getName(), e);
                continue;
            }
            assert value != null;

			//get, description, reset
            gdr(category, field, value);

            //set for boolean
            if (value instanceof ForgeConfigSpec.BooleanValue bValue)
                bool(category, field, bValue);

            // set for PlacmentCheck enum
            if (value.get() instanceof CUConfig.PlacementCheck)
                enumm(category, field, (ForgeConfigSpec.EnumValue<CUConfig.PlacementCheck>) value);

            // set for int
            if (value instanceof ForgeConfigSpec.IntValue iValue)
                integer(category, field, iValue);

            // set for double
            if (value instanceof ForgeConfigSpec.DoubleValue dValue)
				doub(category, field, dValue);

        }
        if (category != null)
			base.then(category);
        CUPlatformFunctions.registerCommand(base);
    }

    private static boolean perms(CommandSourceStack source) {
        return source.hasPermission(4) || !source.getLevel().getServer().isDedicatedServer();
    }


	private static <T> void gdr(LiteralArgumentBuilder<CommandSourceStack> category, Field field, ForgeConfigSpec.ConfigValue<T> value) {
		category.then(literal(field.getName())
			.executes(context -> {
				message(CUConfig.comments.get(field.getName()), context);
				return Command.SINGLE_SUCCESS;
			})
			.then(literal("reset").requires(CreateUnlimitedCommands::perms)
				.executes(context -> {
					if(value.get().equals(value.getDefault())) {
						message("Value is already default!", context, ChatFormatting.RED);
						return Command.SINGLE_SUCCESS;
					}
					value.set(value.getDefault());
					message(field.getName() + " reset to: " + value.get(), context);
					return Command.SINGLE_SUCCESS;
				})
			)
		);
	}

	private static void bool(LiteralArgumentBuilder<CommandSourceStack> category, Field field, ForgeConfigSpec.BooleanValue value) {
		category.then(literal(field.getName())
			.then(literal("set").requires(CreateUnlimitedCommands::perms)
				.then(argument("value", BoolArgumentType.bool())
					.executes(context -> {
						boolean set = BoolArgumentType.getBool(context, "value");
						value.set(set);
						message(field.getName() + " set to: " + set, context);
						return Command.SINGLE_SUCCESS;
					})
				)
			)
		);
	}

	private static void integer(LiteralArgumentBuilder<CommandSourceStack> category, Field field, ForgeConfigSpec.IntValue value) {
		category.then(literal(field.getName())
			.then(literal("set").requires(CreateUnlimitedCommands::perms)
				.then(argument("value", IntegerArgumentType.integer())
					.executes(context -> {
						int set = IntegerArgumentType.getInteger(context, "value");
						value.set(set);
						message(field.getName() + " set to: " + set, context);
						return Command.SINGLE_SUCCESS;
					})
				)
			)
		);
	}

	private static void doub(LiteralArgumentBuilder<CommandSourceStack> category, Field field, ForgeConfigSpec.DoubleValue value) {
		category.then(literal(field.getName())
			.then(literal("set").requires(CreateUnlimitedCommands::perms)
				.then(argument("value", DoubleArgumentType.doubleArg())
					.executes(context -> {
						double set = DoubleArgumentType.getDouble(context, "value");
						value.set(set);
						message(field.getName() + " set to: " + set, context);
						return Command.SINGLE_SUCCESS;
					}))));
	}

	private static <T extends Enum<T>> void enumm(LiteralArgumentBuilder<CommandSourceStack> category, Field field, ForgeConfigSpec.EnumValue<T> value) {
		category.then(literal(field.getName())
			.then(literal("set").requires(CreateUnlimitedCommands::perms)
				.then(argument("value", EnumArgument.enumArgument(value.get().getClass()))
					.executes(context -> {
						T set = (T) context.getArgument("value", value.get().getClass());
						value.set(set);
						message(field.getName() + " set to: " + set.name().toLowerCase(), context);
						return Command.SINGLE_SUCCESS;
					}))));
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
		context.getSource().sendSuccess(nullToEmpty(message), false);
	}
	private static void message(@NotNull String message, CommandContext<CommandSourceStack> context, ChatFormatting color) {
		context.getSource().sendSuccess(Component.literal(message).withStyle(color), false);
	}
}
