package dev.rdh.createunlimited.asm.mixin;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import xyz.wagyourtail.unimined.expect.annotation.PlatformOnly;

import dev.rdh.createunlimited.Util;
import dev.rdh.createunlimited.config.CUConfigs;

import java.util.UUID;
import java.util.function.Supplier;

@Mixin(value = ExtendoGripItem.class)
public abstract class ExtendoGripItemMixin {

	@Dynamic
	@PlatformOnly(PlatformOnly.FABRIC)
	@ModifyExpressionValue(method = {
		"holdingExtendoGripIncreasesRange(Lnet/minecraft/world/entity/LivingEntity;)V",
		"addReachToJoiningPlayersHoldingExtendo(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/nbt/CompoundTag;)V",
	}, at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;rangeModifier:Ljava/util/function/Supplier;"))
	private static Supplier<Multimap<Attribute, AttributeModifier>> modifySingleFabric(Supplier<?> original) {
		return cu$singleRange();
	}

	@Dynamic
	@PlatformOnly(PlatformOnly.FORGE)
	@ModifyExpressionValue(method = {
		"holdingExtendoGripIncreasesRange(Lnet/minecraftforge/event/entity/living/LivingEvent$LivingTickEvent;)V",
		"addReachToJoiningPlayersHoldingExtendo(Lnet/minecraftforge/event/entity/player/PlayerEvent$PlayerLoggedInEvent;)V"
	}, at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;rangeModifier:Ljava/util/function/Supplier;"))
	private static Supplier<Multimap<Attribute, AttributeModifier>> modifySingleForge(Supplier<?> original) {
		return cu$singleRange();
	}

	@Dynamic
	@PlatformOnly(PlatformOnly.FABRIC)
	@ModifyExpressionValue(method = {
		"holdingExtendoGripIncreasesRange(Lnet/minecraft/world/entity/LivingEntity;)V",
		"addReachToJoiningPlayersHoldingExtendo(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/nbt/CompoundTag;)V",
	}, at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;doubleRangeModifier:Ljava/util/function/Supplier;"))
	private static Supplier<Multimap<Attribute, AttributeModifier>> modifyDoubleFabric(Supplier<?> original) {
		return cu$doubleRange();
	}

	@Dynamic
	@PlatformOnly(PlatformOnly.FORGE)
	@ModifyExpressionValue(method = {
		"holdingExtendoGripIncreasesRange(Lnet/minecraftforge/event/entity/living/LivingEvent$LivingTickEvent;)V",
		"addReachToJoiningPlayersHoldingExtendo(Lnet/minecraftforge/event/entity/player/PlayerEvent$PlayerLoggedInEvent;)V"
	}, at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;doubleRangeModifier:Ljava/util/function/Supplier;"))
	private static Supplier<Multimap<Attribute, AttributeModifier>> modifyDoubleForge(Supplier<?> original) {
		return cu$doubleRange();
	}

	@Unique
	private static Supplier<Multimap<Attribute, AttributeModifier>> cu$singleRange() {
		AttributeModifier am = new AttributeModifier(UUID.fromString("7f7dbdb2-0d0d-458a-aa40-ac7633691f66"), "Range modifier",
			Util.orElse(CUConfigs.server.singleExtendoGripRange, 3), AttributeModifier.Operation.ADDITION);
		return Suppliers.memoize(() -> ImmutableMultimap.of(Util.getReachAttribute(), am));
	}

	@Unique
	private static Supplier<Multimap<Attribute, AttributeModifier>> cu$doubleRange() {
		AttributeModifier am = new AttributeModifier(UUID.fromString("8f7dbdb2-0d0d-458a-aa40-ac7633691f66"), "Range modifier",
			Util.orElse(CUConfigs.server.doubleExtendoGripRange, 5), AttributeModifier.Operation.ADDITION);
		return Suppliers.memoize(() -> ImmutableMultimap.of(Util.getReachAttribute(), am));
	}
}