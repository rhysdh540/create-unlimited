package dev.rdh.createunlimited.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.rdh.createunlimited.CUPlatformFunctions;
import dev.rdh.createunlimited.CreateUnlimited;
import dev.rdh.createunlimited.config.CUConfig;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import net.minecraftforge.common.ForgeConfigSpec;

import java.lang.reflect.Field;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * This class is responsible for the code behind the {@code /createunlimited} command.<p>
 * It also registers the command with the game.
 */
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
        LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder = Commands.literal("createunlimited");

        LiteralArgumentBuilder<CommandSourceStack> category = null;
        for (Field field : CUConfig.class.getDeclaredFields()) {
            //skip if not config value or string
            if (field.getType() == ForgeConfigSpec.Builder.class || field.getType() == ForgeConfigSpec.class) continue;

            //change category if needed
            if (field.getType() == String.class) {
                if (category != null) literalArgumentBuilder.then(category);
                category = literal(field.getName());
                continue;
            }
            assert category != null;

            // get config as ConfigValue
            ForgeConfigSpec.ConfigValue<?> value;
            try {
                value = ((ForgeConfigSpec.ConfigValue<?>) field.get(null));
            } catch (IllegalAccessException e) {
                CreateUnlimited.LOGGER.error("Failed to get config value for " + field.getName());
                continue;
            }
            assert value != null;

            // get and reset
            category.then(literal(field.getName()).then(literal("get").executes(context -> {
                context.getSource().sendSuccess(Component.nullToEmpty(field.getName() + " is: " + value.get()), false);
                return Command.SINGLE_SUCCESS;
            })));

            //set for boolean
            if (field.getType() == ForgeConfigSpec.BooleanValue.class)
                category.then(literal(field.getName()).then(literal("set").requires(CreateUnlimitedCommands::perms).then(argument("value", BoolArgumentType.bool()).executes(context -> {
                    boolean set = BoolArgumentType.getBool(context, "value");
                    ((ForgeConfigSpec.BooleanValue) value).set(set);
                    context.getSource().sendSuccess(Component.nullToEmpty(field.getName() + " set to: " + value.get()), false);
                    return Command.SINGLE_SUCCESS;
                })))
                        .then(literal("reset").requires(CreateUnlimitedCommands::perms).executes(context -> {
                            ((ForgeConfigSpec.BooleanValue) value).set(((ForgeConfigSpec.BooleanValue) value).getDefault());
                            context.getSource().sendSuccess(Component.nullToEmpty(field.getName() + " reset to: " + value.get()), false);
                            return Command.SINGLE_SUCCESS;
                        })));

            // set for PlacmentCheck enum
            if (value.get() instanceof CUConfig.PlacementCheck)
                for (CUConfig.PlacementCheck placementCheck : CUConfig.PlacementCheck.values())
                    category.then(literal(field.getName()).then(literal("set").requires(CreateUnlimitedCommands::perms).then(literal(placementCheck.name().toLowerCase()).executes(context -> {
                        ((ForgeConfigSpec.EnumValue<CUConfig.PlacementCheck>) value).set(placementCheck);
                        context.getSource().sendSuccess(Component.nullToEmpty(field.getName() + " set to: " + value.get()), false);
                        return Command.SINGLE_SUCCESS;
                    })))
                            .then(literal("reset").requires(CreateUnlimitedCommands::perms).executes(context -> {
                                ((ForgeConfigSpec.EnumValue<CUConfig.PlacementCheck>) value).set(((ForgeConfigSpec.EnumValue<CUConfig.PlacementCheck>) value).getDefault());
                                context.getSource().sendSuccess(Component.nullToEmpty(field.getName() + " reset to: " + value.get()), false);
                                return Command.SINGLE_SUCCESS;
                            })));

            // set for int
            if (field.getType() == ForgeConfigSpec.IntValue.class)
                category.then(literal(field.getName()).then(literal("set").requires(CreateUnlimitedCommands::perms).then(argument("value", IntegerArgumentType.integer()).executes(context -> {
                    ((ForgeConfigSpec.IntValue) value).set(IntegerArgumentType.getInteger(context, "value"));
                    context.getSource().sendSuccess(Component.nullToEmpty(field.getName() + " set to: " + value.get()), false);
                    return Command.SINGLE_SUCCESS;
                })))
                        .then(literal("reset").requires(CreateUnlimitedCommands::perms).executes(context -> {
                            ((ForgeConfigSpec.IntValue) value).set(((ForgeConfigSpec.IntValue) value).getDefault());
                            context.getSource().sendSuccess(Component.nullToEmpty(field.getName() + " reset to: " + value.get()), false);
                            return Command.SINGLE_SUCCESS;
                        })));

            // set for double
            if (field.getType() == ForgeConfigSpec.DoubleValue.class)
                category.then(literal(field.getName()).then(literal("set").requires(CreateUnlimitedCommands::perms).then(argument("value", DoubleArgumentType.doubleArg()).executes(context -> {
                    ((ForgeConfigSpec.DoubleValue) value).set(DoubleArgumentType.getDouble(context, "value"));
                    context.getSource().sendSuccess(Component.nullToEmpty(field.getName() + " set to: " + value.get()), false);
                    return Command.SINGLE_SUCCESS;
                })))
                        .then(literal("reset").requires(CreateUnlimitedCommands::perms).executes(context -> {
                            ((ForgeConfigSpec.DoubleValue) value).set(((ForgeConfigSpec.DoubleValue) value).getDefault());
                            context.getSource().sendSuccess(Component.nullToEmpty(field.getName() + " reset to: " + value.get()), false);
                            return Command.SINGLE_SUCCESS;
                        })));
        }
        if (category != null) literalArgumentBuilder.then(category);
        CUPlatformFunctions.registerCommand(literalArgumentBuilder);
    }

    /**
     * Checks if the player has permission to change config values.
     * @param source the source of the command
     * @return true if the player has permission to change config values (is operator or in singleplayer), false otherwise
     */
    public static boolean perms(CommandSourceStack source) {
        return source.hasPermission(4) || !source.getLevel().getServer().isDedicatedServer();
    }
}
