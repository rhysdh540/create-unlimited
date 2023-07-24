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
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * copied from froge since the config api port removed it
 * <p>
 * oh yeah i also added configurable lowercase because yes
 */
public class EnumArgument<T extends Enum<T>> implements ArgumentType<T> {
	private static final Dynamic2CommandExceptionType INVALID_ENUM = new Dynamic2CommandExceptionType(
		(found, constants) -> Component.translatable("commands.forge.arguments.enum.invalid", constants, found));
	private final Class<T> enumClass;
	private final boolean lowercase;

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
		public Template unpack(EnumArgument<T> argument) {
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
			public EnumArgument<T> instantiate(CommandBuildContext ctx) {
				return new EnumArgument<>(this.enumClass, this.lowercase);
			}

			@Override
			public ArgumentTypeInfo<EnumArgument<T>, ?> type() {
				return Info.this;
			}
		}
	}

	private String lowercase(String s) {
		return lowercase ? s.toLowerCase() : s;
	}
	private String unlowercase(String s) {
		return lowercase ? s.toUpperCase() : s;
	}
}