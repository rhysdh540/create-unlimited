package dev.rdh.createunlimited.mixin;

import com.google.common.collect.Multimap;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import dev.architectury.injectables.annotations.PlatformOnly;

import java.util.function.Supplier;

import static dev.rdh.createunlimited.Util.doubleRange;
import static dev.rdh.createunlimited.Util.singleRange;

@SuppressWarnings({"unused", "UnresolvedMixinReference", "MixinAnnotationTarget"})
@Mixin(value = ExtendoGripItem.class, remap = false)
public abstract class ExtendoGripItemMixin {

	// the annotation processor is a little broken so we have to specify `remap = false` and use the obfuscated names

	@ModifyExpressionValue(method = {
		"holdingExtendoGripIncreasesRange(Lnet/minecraft/world/entity/LivingEntity;)V",
		"holdingExtendoGripIncreasesRange(Lnet/minecraft/class_1309;)V",

		"addReachToJoiningPlayersHoldingExtendo(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/nbt/CompoundTag;)V",
		"addReachToJoiningPlayersHoldingExtendo(Lnet/minecraft/class_1297;Lnet/minecraft/class_2487;)V",
	}, at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;rangeModifier:Ljava/util/function/Supplier;"))
	@PlatformOnly(PlatformOnly.FABRIC)
	private static Supplier<Multimap<Attribute, AttributeModifier>> modifySingleFabric(Supplier<?> original) {
		return singleRange();
	}

	@ModifyExpressionValue(method = {
		"holdingExtendoGripIncreasesRange(Lnet/minecraftforge/event/entity/living/LivingEvent$LivingTickEvent;)V",
		"addReachToJoiningPlayersHoldingExtendo(Lnet/minecraftforge/event/entity/player/PlayerEvent$PlayerLoggedInEvent;)V"
	}, at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;rangeModifier:Ljava/util/function/Supplier;"))
	@PlatformOnly(PlatformOnly.FORGE)
	private static Supplier<Multimap<Attribute, AttributeModifier>> modifySingleForge(Supplier<?> original) {
		return singleRange();
	}

	@ModifyExpressionValue(method = {
		"holdingExtendoGripIncreasesRange(Lnet/minecraft/world/entity/LivingEntity;)V",
		"holdingExtendoGripIncreasesRange(Lnet/minecraft/class_1309;)V",
		"addReachToJoiningPlayersHoldingExtendo(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/nbt/CompoundTag;)V",
		"addReachToJoiningPlayersHoldingExtendo(Lnet/minecraft/class_1297;Lnet/minecraft/class_2487;)V",
	}, at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;doubleRangeModifier:Ljava/util/function/Supplier;"))
	@PlatformOnly(PlatformOnly.FABRIC)
	private static Supplier<Multimap<Attribute, AttributeModifier>> modifyDoubleFabric(Supplier<?> original) {
		return doubleRange();
	}

	@ModifyExpressionValue(method = {
		"holdingExtendoGripIncreasesRange(Lnet/minecraftforge/event/entity/living/LivingEvent$LivingTickEvent;)V",
		"addReachToJoiningPlayersHoldingExtendo(Lnet/minecraftforge/event/entity/player/PlayerEvent$PlayerLoggedInEvent;)V"
	}, at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;doubleRangeModifier:Ljava/util/function/Supplier;"))
	@PlatformOnly(PlatformOnly.FORGE)
	private static Supplier<Multimap<Attribute, AttributeModifier>> modifyDoubleForge(Supplier<?> original) {
		return doubleRange();
	}
}