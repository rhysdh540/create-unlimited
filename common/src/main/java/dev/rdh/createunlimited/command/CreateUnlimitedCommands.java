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
import net.minecraft.commands.Commands;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

import net.minecraftforge.common.ForgeConfigSpec;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mutable;

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

            // get and description
            category.then(literal(field.getName())
				.executes(context -> {
					message(CUConfig.comments.get(field.getName()), context);
					return Command.SINGLE_SUCCESS;
				})
				.then(literal("get").executes(context -> {
					message(field.getName() + " is: " + value.get(), context);
					return Command.SINGLE_SUCCESS;
				})));

            //set for boolean
            if (field.getType() == ForgeConfigSpec.BooleanValue.class)
                category.then(literal(field.getName()).then(literal("set").requires(CreateUnlimitedCommands::perms).then(argument("value", BoolArgumentType.bool()).executes(context -> {
                    boolean set = BoolArgumentType.getBool(context, "value");
                    ((ForgeConfigSpec.BooleanValue) value).set(set);
                    message(field.getName() + " set to: " + set, context);
                    return Command.SINGLE_SUCCESS;
                })))
                        .then(literal("reset").requires(CreateUnlimitedCommands::perms).executes(context -> {
                            ((ForgeConfigSpec.BooleanValue) value).set(((ForgeConfigSpec.BooleanValue) value).getDefault());
                            message(field.getName() + " reset to: " + value.get(), context);
                            return Command.SINGLE_SUCCESS;
                        })));

            // set for PlacmentCheck enum
            if (value.get() instanceof CUConfig.PlacementCheck)
                for (CUConfig.PlacementCheck placementCheck : CUConfig.PlacementCheck.values())
                    category.then(literal(field.getName()).then(literal("set").requires(CreateUnlimitedCommands::perms).then(literal(placementCheck.name().toLowerCase()).executes(context -> {
                        ((ForgeConfigSpec.EnumValue<CUConfig.PlacementCheck>) value).set(placementCheck);
                        message(field.getName() + " set to: " + placementCheck.name().toLowerCase(), context);
                        return Command.SINGLE_SUCCESS;
                    })))
                            .then(literal("reset").requires(CreateUnlimitedCommands::perms).executes(context -> {
                                ((ForgeConfigSpec.EnumValue<CUConfig.PlacementCheck>) value).set(((ForgeConfigSpec.EnumValue<CUConfig.PlacementCheck>) value).getDefault());
                                message(field.getName() + " reset to: " + ((CUConfig.PlacementCheck) value.get()).name().toLowerCase(), context);
                                return Command.SINGLE_SUCCESS;
                            })));

            // set for int
            if (field.getType() == ForgeConfigSpec.IntValue.class)
                category.then(literal(field.getName()).then(literal("set").requires(CreateUnlimitedCommands::perms).then(argument("value", IntegerArgumentType.integer()).executes(context -> {
                    ((ForgeConfigSpec.IntValue) value).set(IntegerArgumentType.getInteger(context, "value"));
                    message(field.getName() + " set to: " + value.get(), context);
                    return Command.SINGLE_SUCCESS;
                })))
                        .then(literal("reset").requires(CreateUnlimitedCommands::perms).executes(context -> {
                            ((ForgeConfigSpec.IntValue) value).set(((ForgeConfigSpec.IntValue) value).getDefault());
                            message(field.getName() + " reset to: " + value.get(), context);
                            return Command.SINGLE_SUCCESS;
                        })));

            // set for double
            if (field.getType() == ForgeConfigSpec.DoubleValue.class)
                category.then(literal(field.getName()).then(literal("set").requires(CreateUnlimitedCommands::perms).then(argument("value", DoubleArgumentType.doubleArg()).executes(context -> {
                    ((ForgeConfigSpec.DoubleValue) value).set(DoubleArgumentType.getDouble(context, "value"));
                    message(field.getName() + " set to: " + value.get(), context);
                    return Command.SINGLE_SUCCESS;
                })))
                        .then(literal("reset").requires(CreateUnlimitedCommands::perms).executes(context -> {
                            ((ForgeConfigSpec.DoubleValue) value).set(((ForgeConfigSpec.DoubleValue) value).getDefault());
							message(field.getName() + " reset to: " + value.get(), context);
                            return Command.SINGLE_SUCCESS;
                        })));
        }
        if (category != null) base.then(category);
        CUPlatformFunctions.registerCommand(base);
    }

    /**
     * Checks if the player has permission to change config values.
     * @param source the source of the command
     * @return true if the player has permission to change config values (is operator or in singleplayer), false otherwise
     */
    private static boolean perms(CommandSourceStack source) {
        return source.hasPermission(4) || !source.getLevel().getServer().isDedicatedServer();
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
