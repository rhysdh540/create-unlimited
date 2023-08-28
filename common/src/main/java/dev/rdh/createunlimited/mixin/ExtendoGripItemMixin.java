package dev.rdh.createunlimited.mixin;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;

import dev.rdh.createunlimited.Util;
import dev.rdh.createunlimited.config.CUConfigs;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;
import java.util.function.Supplier;

@Mixin(ExtendoGripItem.class)
public class ExtendoGripItemMixin {

	@Unique
	private static Supplier<Multimap<Attribute, AttributeModifier>> createUnlimited$singleRange() {
		AttributeModifier am = new AttributeModifier(UUID.fromString("7f7dbdb2-0d0d-458a-aa40-ac7633691f66"), "Range modifier",
				CUConfigs.server().singleExtendoGripRange.get(), AttributeModifier.Operation.ADDITION);
		return Suppliers.memoize(() -> ImmutableMultimap.of(Util.getReachAttribute(), am));
	}

	@Unique
	private static Supplier<Multimap<Attribute, AttributeModifier>> createUnlimited$doubleRange() {
		AttributeModifier am = new AttributeModifier(UUID.fromString("8f7dbdb2-0d0d-458a-aa40-ac7633691f66"), "Range modifier",
				CUConfigs.server().doubleExtendoGripRange.get(), AttributeModifier.Operation.ADDITION);
		return Suppliers.memoize(() -> ImmutableMultimap.of(Util.getReachAttribute(), am));
	}

	@Redirect(method = "holdingExtendoGripIncreasesRange", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;rangeModifier:Ljava/util/function/Supplier;"))
	private static Supplier<Multimap<Attribute, AttributeModifier>> mainSingle() {
		return createUnlimited$singleRange();
	}

	@Redirect(method = "holdingExtendoGripIncreasesRange", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;doubleRangeModifier:Ljava/util/function/Supplier;"))
	private static Supplier<Multimap<Attribute, AttributeModifier>> mainDouble() {
		return createUnlimited$doubleRange();
	}

	@Redirect(method = "addReachToJoiningPlayersHoldingExtendo", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;rangeModifier:Ljava/util/function/Supplier;"))
	private static Supplier<Multimap<Attribute, AttributeModifier>> joinSingle() {
		return createUnlimited$singleRange();
	}

	@Redirect(method = "addReachToJoiningPlayersHoldingExtendo", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;doubleRangeModifier:Ljava/util/function/Supplier;"))
	private static Supplier<Multimap<Attribute, AttributeModifier>> joinDouble() {
		return createUnlimited$doubleRange();
	}
}
