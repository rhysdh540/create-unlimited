package dev.rdh.createunlimited.asm.mixin;

import com.google.common.base.Supplier;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;

import it.unimi.dsi.fastutil.doubles.Double2ObjectOpenHashMap;

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
		double newAmount = CUConfig.getOrDefault(CUConfig.instance.singleExtendoGripRange, original.getAmount());
		AttributeModifier modifier = cu$cachedModifiers.get(newAmount);
		if (modifier == null) {
			modifier = new AttributeModifier(original.getId(), original.getName(), newAmount, original.getOperation());
			cu$cachedModifiers.put(newAmount, modifier);
		}
		return modifier;
	}

	@ModifyExpressionValue(method = "lambda$static$1", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;doubleRangeAttributeModifier:Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;"))
	private static AttributeModifier modifyDoubleRangeAttributeModifier(AttributeModifier original) {
		double newAmount = CUConfig.getOrDefault(CUConfig.instance.doubleExtendoGripRange, original.getAmount());
		AttributeModifier modifier = cu$cachedModifiers.get(-newAmount);
		if (modifier == null) {
			modifier = new AttributeModifier(original.getId(), original.getName(), newAmount, original.getOperation());
			cu$cachedModifiers.put(-newAmount, modifier);
		}
		return modifier;
	}

	// don't memoize
	@Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/google/common/base/Suppliers;memoize(Lcom/google/common/base/Supplier;)Lcom/google/common/base/Supplier;"))
	private static Supplier<?> modifySingleRangeAttributeModifier(Supplier<?> original) {
		return original;
	}
}