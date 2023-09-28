package dev.rdh.createunlimited.mixin;

import com.google.common.collect.Multimap;

import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

import static dev.rdh.createunlimited.Util.doubleRange;
import static dev.rdh.createunlimited.Util.singleRange;

@Mixin(ExtendoGripItem.class)
@SuppressWarnings("UnresolvedMixinReference")
public abstract class ExtendoGripItemMixin {

	@Redirect(method = {
		"holdingExtendoGripIncreasesRange(Lnet/minecraft/world/entity/LivingEntity;)V", // fabric
		"holdingExtendoGripIncreasesRange(Lnet/minecraftforge/event/entity/living/LivingEvent$LivingTickEvent;)V" // forge
	}, at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;rangeModifier:Ljava/util/function/Supplier;"))
	private static Supplier<Multimap<Attribute, AttributeModifier>> mainSingle() {
		return singleRange();
	}

	@Redirect(method = {
		"holdingExtendoGripIncreasesRange(Lnet/minecraft/world/entity/LivingEntity;)V", // fabric
		"holdingExtendoGripIncreasesRange(Lnet/minecraftforge/event/entity/living/LivingEvent$LivingTickEvent;)V" // forge
	}, at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;doubleRangeModifier:Ljava/util/function/Supplier;"))
	private static Supplier<Multimap<Attribute, AttributeModifier>> mainDouble() {
		return doubleRange();
	}

	@Redirect(method = {
		"addReachToJoiningPlayersHoldingExtendo(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/nbt/CompoundTag;)V", // fabric
		"addReachToJoiningPlayersHoldingExtendo(Lnet/minecraftforge/event/entity/player/PlayerEvent$PlayerLoggedInEvent;)V" // forge
	}, at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;rangeModifier:Ljava/util/function/Supplier;"))
	private static Supplier<Multimap<Attribute, AttributeModifier>> joinSingle() {
		return singleRange();
	}

	@Redirect(method = {
		"addReachToJoiningPlayersHoldingExtendo(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/nbt/CompoundTag;)V", // fabric
		"addReachToJoiningPlayersHoldingExtendo(Lnet/minecraftforge/event/entity/player/PlayerEvent$PlayerLoggedInEvent;)V" // forge
	}, at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;doubleRangeModifier:Ljava/util/function/Supplier;"))
	private static Supplier<Multimap<Attribute, AttributeModifier>> joinDouble() {
		return doubleRange();
	}
}