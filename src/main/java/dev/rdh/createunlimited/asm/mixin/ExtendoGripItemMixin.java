package dev.rdh.createunlimited.asm.mixin;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import dev.rdh.createunlimited.Util;
import dev.rdh.createunlimited.config.CUConfig;

import java.util.function.Supplier;

@Mixin(value = ExtendoGripItem.class)
public abstract class ExtendoGripItemMixin {

	@ModifyExpressionValue(method = {
		"holdingExtendoGripIncreasesRange",
		"addReachToJoiningPlayersHoldingExtendo"
	}, at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;rangeModifier:Ljava/util/function/Supplier;"))
	private static Supplier<Multimap<Attribute, AttributeModifier>> modifySingleForge(Supplier<?> original) {
		return cu$singleRange();
	}

	@ModifyExpressionValue(method = {
		"holdingExtendoGripIncreasesRange",
		"addReachToJoiningPlayersHoldingExtendo"
	}, at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;doubleRangeModifier:Ljava/util/function/Supplier;"))
	private static Supplier<Multimap<Attribute, AttributeModifier>> modifyDoubleForge(Supplier<?> original) {
		return cu$doubleRange();
	}

	@Unique
	private static Supplier<Multimap<Attribute, AttributeModifier>> cu$singleRange() {
		AttributeModifier am = new AttributeModifier(Create.asResource("double_range_attribute_modifier"),
			CUConfig.getOrDefault(CUConfig.instance.singleExtendoGripRange, 3), Operation.ADD_VALUE);
		return Suppliers.memoize(() -> ImmutableMultimap.of(Util.getReachAttribute(), am));
	}

	@Unique
	private static Supplier<Multimap<Attribute, AttributeModifier>> cu$doubleRange() {
		AttributeModifier am = new AttributeModifier(Create.asResource("single_range_attribute_modifier"),
			CUConfig.getOrDefault(CUConfig.instance.doubleExtendoGripRange, 5), AttributeModifier.Operation.ADD_VALUE);
		return Suppliers.memoize(() -> ImmutableMultimap.of(Util.getReachAttribute(), am));
	}
}