/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package dev.rdh.createunlimited.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import dev.rdh.createunlimited.CreateUnlimited;
import dev.rdh.createunlimited.Util;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * copied from froge
 * <p>
 * oh yeah i also added configurable lowercase because yes
 */
public class EnumArgument<T extends Enum<T>> implements ArgumentType<T> {
	private static final Dynamic2CommandExceptionType INVALID_ENUM = new Dynamic2CommandExceptionType(
		(found, constants) -> Component.literal(String.format("Invalid enum value '%s', expected one of: %s", found, constants)));
	private final Class<T> enumClass;
	private final boolean lowercase;

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void init() {
		Util.INSTANCE.registerArgument(EnumArgument.class, new EnumArgument.Info(), CreateUnlimited.asResource("enumargument"));
	}

	public static <R extends Enum<R>> EnumArgument<R> enumArg(Class<R> enumClass, boolean lowercase) {
		return new EnumArgument<>(enumClass, lowercase);
	}

	private EnumArgument(final Class<T> enumClass, final boolean lowercase) {
		this.enumClass = enumClass;
		this.lowercase = lowercase;
	}

	@Override
	public T parse(final StringReader reader) throws CommandSyntaxException {
		String name = reader.readUnquotedString();
		try {
			return Enum.valueOf(enumClass, unlowercase(name));
		} catch (IllegalArgumentException e) {
			throw INVALID_ENUM.createWithContext(reader, name, Arrays.toString(Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).map(this::lowercase).toArray()));
		}
	}

	public static <R extends Enum<R>> R getEnum(CommandContext<?> context, String name, Class<R> enumClass) {
		return context.getArgument(name, enumClass);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggest(Stream.of(enumClass.getEnumConstants()).map(Enum::name).map(this::lowercase), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return Stream.of(enumClass.getEnumConstants()).map(Enum::name).map(this::lowercase).collect(Collectors.toList());
	}

	public static class Info<T extends Enum<T>> implements ArgumentTypeInfo<EnumArgument<T>, Info<T>.Template> {
		@Override
		public void serializeToNetwork(Template template, FriendlyByteBuf buffer) {
			buffer.writeUtf(template.enumClass.getName());
			buffer.writeBoolean(template.lowercase);
		}

		@Override
		@SuppressWarnings("unchecked")
		public Template deserializeFromNetwork(FriendlyByteBuf buffer) {
			try {
				String name = buffer.readUtf();
				boolean l = buffer.readBoolean();
				return new Template((Class<T>) Class.forName(name), l);
			}
			catch (ClassNotFoundException e) {
				return null;
			}
		}

		@Override
		public void serializeToJson(Template template, JsonObject json) {
			json.addProperty("enum", template.enumClass.getName());
		}

		@Override
		public @NotNull Template unpack(EnumArgument<T> argument) {
			return new Template(argument.enumClass, argument.lowercase);
		}

		public class Template implements ArgumentTypeInfo.Template<EnumArgument<T>> {
			final Class<T> enumClass;
			final boolean lowercase;

			Template(Class<T> enumClass, boolean lowercase) {
				this.enumClass = enumClass;
				this.lowercase = lowercase;
			}

			@Override
			public @NotNull EnumArgument<T> instantiate(@NotNull CommandBuildContext ctx) {
				return new EnumArgument<>(this.enumClass, this.lowercase);
			}

			@Override
			public @NotNull ArgumentTypeInfo<EnumArgument<T>, ?> type() {
				return Info.this;
			}
		}
	}

	private String lowercase(String s) {
		return lowercase ? s.toLowerCase() : s;
	}
	private String unlowercase(String s) {
		if(!lowercase)
			return s;
		return Arrays.stream(enumClass.getEnumConstants())
			.map(Enum::name)
			.filter(n -> n.equalsIgnoreCase(s))
			.findFirst()
			.orElse(s);
	}
}