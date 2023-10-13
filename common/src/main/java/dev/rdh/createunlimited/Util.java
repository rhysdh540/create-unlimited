package dev.rdh.createunlimited;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.arguments.ArgumentType;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.architectury.injectables.annotations.ExpectPlatform;

import dev.rdh.createunlimited.config.CUConfigs;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction.Axis;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.UUID;
import java.util.function.Supplier;

import manifold.rt.api.NoBootstrap;

@NoBootstrap
public class Util {

	@ExpectPlatform
	public static String getVersion() {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static boolean isDevEnv() {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static String platformName() {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void registerCommand(LiteralArgumentBuilder<CommandSourceStack> command) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void registerConfig(ModConfig.Type type, IConfigSpec<?> spec) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>>
	void registerArgument(Class<A> clazz, I info, ResourceLocation id) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static Attribute getReachAttribute() {
		throw new AssertionError();
	}

	public static Supplier<Multimap<Attribute, AttributeModifier>> singleRange() {
		AttributeModifier am = new AttributeModifier(UUID.fromString("7f7dbdb2-0d0d-458a-aa40-ac7633691f66"), "Range modifier",
				CUConfigs.server().singleExtendoGripRange.get(), AttributeModifier.Operation.ADDITION);
		return Suppliers.memoize(() -> ImmutableMultimap.of(getReachAttribute(), am));
	}

	public static Supplier<Multimap<Attribute, AttributeModifier>> doubleRange() {
		AttributeModifier am = new AttributeModifier(UUID.fromString("8f7dbdb2-0d0d-458a-aa40-ac7633691f66"), "Range modifier",
				CUConfigs.server().doubleExtendoGripRange.get(), AttributeModifier.Operation.ADDITION);
		return Suppliers.memoize(() -> ImmutableMultimap.of(getReachAttribute(), am));
	}

	@SuppressWarnings("SuspiciousNameCombination") // javac doesn't like when we pass a value called "y" to a method that expects a value called "x"
	public static double[] intersect(Vec3 p1, Vec3 p2, Vec3 r, Vec3 s, Axis plane) {
		if (plane == Axis.X) {
			p1 = new Vec3(p1.y, 0, p1.z);
			p2 = new Vec3(p2.y, 0, p2.z);
			r = new Vec3(r.y, 0, r.z);
			s = new Vec3(s.y, 0, s.z);
		}

		if (plane == Axis.Z) {
			p1 = new Vec3(p1.x, 0, p1.y);
			p2 = new Vec3(p2.x, 0, p2.y);
			r = new Vec3(r.x, 0, r.y);
			s = new Vec3(s.x, 0, s.y);
		}

		Vec3 qminusp = p2.subtract(p1);
		double rcs = r.x * s.z - r.z * s.x;
		Vec3 rdivrcs = r.scale(1 / rcs);
		Vec3 sdivrcs = s.scale(1 / rcs);
		double t = qminusp.x * sdivrcs.z - qminusp.z * sdivrcs.x;
		double u = qminusp.x * rdivrcs.z - qminusp.z * rdivrcs.x;
		return new double[]{t, u};
	}

	public static ItemStack copyStackWithSize(ItemStack stack, int size) {
		if (size == 0) return ItemStack.EMPTY;
		ItemStack copy = stack.copy();
		copy.setCount(size);
		return copy;
	}
}
