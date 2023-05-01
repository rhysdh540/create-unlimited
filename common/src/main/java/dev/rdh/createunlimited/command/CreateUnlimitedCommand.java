package dev.rdh.createunlimited.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.rdh.createunlimited.CUPlatformFunctions;
import dev.rdh.createunlimited.config.CUConfig;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import java.lang.reflect.Field;

import static net.minecraft.commands.Commands.*;

public class CreateUnlimitedCommand {
    public static void register(){
        LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder = Commands.literal("createunlimited");

        for(Field field : CUConfig.class.getDeclaredFields()) {
            if(field.getType() == String.class || field.getType() == ForgeConfigSpec.Builder.class || field.getType() == ForgeConfigSpec.class)
                continue;

            // get and reset
            literalArgumentBuilder.then(literal(field.getName())
                    .executes(context -> {
                        ForgeConfigSpec.ConfigValue<?> value;
                        try {
                            value = ((ForgeConfigSpec.ConfigValue<?>)field.get(null));
                        } catch (IllegalAccessException e) {
                            context.getSource().sendFailure(Component.literal("Failed to get config value for " + field.getName()));
                            return 0;
                        }
                        assert value != null;
                        context.getSource().sendSuccess(Component.nullToEmpty(field.getName() + " is: " + value.get()), false);
                        return Command.SINGLE_SUCCESS;
                    })
                    .then(literal("reset").requires(source -> source.hasPermission(4)).executes(context -> {
                        ForgeConfigSpec.ConfigValue<?> value;
                        try {
                            value = ((ForgeConfigSpec.ConfigValue<?>)field.get(null));
                        } catch (IllegalAccessException e) {
                            context.getSource().sendFailure(Component.literal("Failed to get config value for " + field.getName()));
                            return 0;
                        }
                        assert value != null;
                        if(field.getType() == ForgeConfigSpec.BooleanValue.class)
                            ((ForgeConfigSpec.BooleanValue)value).set(((ForgeConfigSpec.BooleanValue)value).getDefault());
                        else if(field.getType() == ForgeConfigSpec.IntValue.class)
                            ((ForgeConfigSpec.IntValue)value).set(((ForgeConfigSpec.IntValue)value).getDefault());
                        else if(field.getType() == ForgeConfigSpec.DoubleValue.class)
                            ((ForgeConfigSpec.DoubleValue)value).set(((ForgeConfigSpec.DoubleValue)value).getDefault());
                        else if(value.get() instanceof CUConfig.PlacementCheck)
                            ((ForgeConfigSpec.EnumValue<CUConfig.PlacementCheck>)value).set(CUConfig.PlacementCheck.ON);
                        context.getSource().sendSuccess(Component.nullToEmpty(field.getName() + " reset to: " + value.get()), false);
                        return Command.SINGLE_SUCCESS;
            })));

            //set for boolean
            if(field.getType() == ForgeConfigSpec.BooleanValue.class)
                literalArgumentBuilder.then(literal(field.getName())
                        .then(literal("set").requires(source -> source.hasPermission(4))
                                .then(argument("value", BoolArgumentType.bool()).executes(context -> {
                                    boolean set = BoolArgumentType.getBool(context, "value");
                                    ForgeConfigSpec.BooleanValue value;
                                    try {
                                        value = ((ForgeConfigSpec.BooleanValue) field.get(null));
                                    } catch (IllegalAccessException e) {
                                        context.getSource().sendFailure(Component.literal("Failed to get config value for " + field.getName()));
                                        return 0;
                                    }
                                    assert value != null;
                                    value.set(set);
                                    context.getSource().sendSuccess(Component.nullToEmpty(field.getName() + " set to: " + value.get()), false);
                                    return Command.SINGLE_SUCCESS;
                                }))));

            // set for PlacmentCheck enum
            else if(field.getType() == ForgeConfigSpec.EnumValue.class)
                for(CUConfig.PlacementCheck placementCheck : CUConfig.PlacementCheck.values())
                    literalArgumentBuilder.then(literal(field.getName())
                            .then(literal("set").requires(source -> source.hasPermission(4))
                                    .then(literal(placementCheck.name().toLowerCase()).executes(context -> {
                                        ForgeConfigSpec.EnumValue<CUConfig.PlacementCheck> value;
                                        try {
                                            value = ((ForgeConfigSpec.EnumValue<CUConfig.PlacementCheck>) field.get(null));
                                        } catch (IllegalAccessException e) {
                                            context.getSource().sendFailure(Component.literal("Failed to get config value for " + field.getName()));
                                            return 0;
                                        }
                                        assert value != null;
                                        value.set(placementCheck);
                                        context.getSource().sendSuccess(Component.nullToEmpty(field.getName() + " set to: " + value.get()), false);
                                        return Command.SINGLE_SUCCESS;
                                    }))));

            // set for int/double
            else literalArgumentBuilder.then(literal(field.getName())
                        .then(literal("set").requires(source -> source.hasPermission(4))
                                .then(argument("value", StringArgumentType.string()).executes(context -> {
                                    String set = StringArgumentType.getString(context, "value");
                                    ForgeConfigSpec.ConfigValue<?> value;
                                    try {
                                        value = ((ForgeConfigSpec.ConfigValue<?>) field.get(null));
                                    } catch (IllegalAccessException e) {
                                        context.getSource().sendFailure(Component.literal("Failed to get config value for " + field.getName()));
                                        return 0;
                                    }
                                    assert value != null;
                                    if(field.getType() == ForgeConfigSpec.IntValue.class)
                                        ((ForgeConfigSpec.IntValue)value).set(Integer.parseInt(set));
                                    else if(field.getType() == ForgeConfigSpec.DoubleValue.class)
                                        ((ForgeConfigSpec.DoubleValue)value).set(Double.parseDouble(set));
                                    context.getSource().sendSuccess(Component.nullToEmpty(field.getName() + " set to: " + value.get()), false);
                                    return Command.SINGLE_SUCCESS;
                                }))));
        }
        CUPlatformFunctions.registerCommand(literalArgumentBuilder);
     }
}
