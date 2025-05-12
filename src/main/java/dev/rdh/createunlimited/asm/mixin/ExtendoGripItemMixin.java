package dev.rdh.createunlimited.asm.mixin;

import com.google.common.base.Supplier;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.rdh.createunlimited.config.CUConfig;

@Mixin(value = ExtendoGripItem.class)
public abstract class ExtendoGripItemMixin {

	@ModifyExpressionValue(method = "lambda$static$0", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;singleRangeAttributeModifier:Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;"))
	private static AttributeModifier modifySingleRangeAttributeModifier(AttributeModifier original) {
		double newAmount = CUConfig.getOrDefault(CUConfig.instance.singleExtendoGripRange, original.getAmount());
		return new AttributeModifier(original.getId(), original.getName(), newAmount, original.getOperation());
	}

	@ModifyExpressionValue(method = "lambda$static$1", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripItem;doubleRangeAttributeModifier:Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;"))
	private static AttributeModifier modifyDoubleRangeAttributeModifier(AttributeModifier original) {
		double newAmount = CUConfig.getOrDefault(CUConfig.instance.doubleExtendoGripRange, original.getAmount());
		return new AttributeModifier(original.getId(), original.getName(), newAmount, original.getOperation());
	}

	// don't memoize
	@Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/google/common/base/Suppliers;memoize(Lcom/google/common/base/Supplier;)Lcom/google/common/base/Supplier;"))
	private static Supplier<?> modifySingleRangeAttributeModifier(Supplier<?> original) {
		return original;
	}
}