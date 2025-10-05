package dev.rdh.createunlimited.asm.mixin;

import com.google.common.base.Supplier;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;

import it.unimi.dsi.fastutil.doubles.Double2ObjectOpenHashMap;

import net.createmod.catnip.config.ConfigBase.ConfigFloat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.rdh.createunlimited.config.CUConfig;

@Mixin(ExtendoGripItem.class)
public abstract class ExtendoGripItemMixin {

	// keys <0 for double, >0 for single
	@Unique private static final Double2ObjectMap<AttributeModifier> cu$cachedModifiers = new Double2ObjectOpenHashMap<>();

	@ModifyExpressionValue(method = "lambda$static$0", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;singleRangeAttributeModifier:Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;"))
	private static AttributeModifier modifySingleRangeAttributeModifier(AttributeModifier original) {
		return modify(original, true);
	}

	@ModifyExpressionValue(method = "lambda$static$1", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;doubleRangeAttributeModifier:Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;"))
	private static AttributeModifier modifyDoubleRangeAttributeModifier(AttributeModifier original) {
		return modify(original, false);
	}

	@Unique
	private static AttributeModifier modify(AttributeModifier original, boolean isSingle) {
		double originalAmount = #if MC < 21 original.getAmount() #else original.amount() #endif;

		ConfigFloat config = isSingle ? CUConfig.instance.singleExtendoGripRange : CUConfig.instance.doubleExtendoGripRange;
		double newAmount = CUConfig.getOrDefault(config, originalAmount);
		AttributeModifier modifier = cu$cachedModifiers.get(isSingle ? newAmount : -newAmount);
		if (modifier == null) {
			var originalId = #if MC < 21 original.getId() #else original.id() #endif;
			Operation originalOperation = #if MC < 21 original.getOperation() #else original.operation() #endif;
			modifier = new AttributeModifier(originalId, #if MC < 21 original.getName(), #endif newAmount, originalOperation);
			cu$cachedModifiers.put(isSingle ? newAmount : -newAmount, modifier);
		}
		return modifier;
	}

	@Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/google/common/base/Suppliers;memoize(Lcom/google/common/base/Supplier;)Lcom/google/common/base/Supplier;", remap = false))
	private static Supplier<?> noMemoize(Supplier<?> original) {
		return original;
	}
}