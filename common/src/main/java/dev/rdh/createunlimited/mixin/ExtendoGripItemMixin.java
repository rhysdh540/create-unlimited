package dev.rdh.createunlimited.mixin;

import com.google.common.collect.Multimap;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

import static dev.rdh.createunlimited.Util.doubleRange;
import static dev.rdh.createunlimited.Util.singleRange;

@SuppressWarnings({"unused", "UnresolvedMixinReference"})
@Mixin(value = ExtendoGripItem.class, remap = false)
public abstract class ExtendoGripItemMixin {

	// the annotation processor is a little broken so we have to specify `remap = false` and use the obfuscated names

	@ModifyExpressionValue(method = {
		"holdingExtendoGripIncreasesRange(Lnet/minecraft/world/entity/LivingEntity;)V", // fabric
		"holdingExtendoGripIncreasesRange(Lnet/minecraft/class_1309;)V", // fabric obf
		"holdingExtendoGripIncreasesRange(Lnet/minecraftforge/event/entity/living/LivingEvent$LivingTickEvent;)V", // forge

		"addReachToJoiningPlayersHoldingExtendo(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/nbt/CompoundTag;)V", // fabric
		"addReachToJoiningPlayersHoldingExtendo(Lnet/minecraft/class_1297;Lnet/minecraft/class_2487;)V", // fabric obf
		"addReachToJoiningPlayersHoldingExtendo(Lnet/minecraftforge/event/entity/player/PlayerEvent$PlayerLoggedInEvent;)V" // forge
	}, at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;rangeModifier:Ljava/util/function/Supplier;"))
	private static Supplier<Multimap<Attribute, AttributeModifier>> modifySingle(Supplier<?> original) {
		return singleRange();
	}

	@ModifyExpressionValue(method = {
		"holdingExtendoGripIncreasesRange(Lnet/minecraft/world/entity/LivingEntity;)V", // fabric
		"holdingExtendoGripIncreasesRange(Lnet/minecraft/class_1309;)V", // fabric obf
		"holdingExtendoGripIncreasesRange(Lnet/minecraftforge/event/entity/living/LivingEvent$LivingTickEvent;)V", // forge

		"addReachToJoiningPlayersHoldingExtendo(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/nbt/CompoundTag;)V", // fabric
		"addReachToJoiningPlayersHoldingExtendo(Lnet/minecraft/class_1297;Lnet/minecraft/class_2487;)V", // fabric obf
		"addReachToJoiningPlayersHoldingExtendo(Lnet/minecraftforge/event/entity/player/PlayerEvent$PlayerLoggedInEvent;)V" // forge
	}, at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;doubleRangeModifier:Ljava/util/function/Supplier;"))
	private static Supplier<Multimap<Attribute, AttributeModifier>> modifyDouble(Supplier<?> original) {
		return doubleRange();
	}
}