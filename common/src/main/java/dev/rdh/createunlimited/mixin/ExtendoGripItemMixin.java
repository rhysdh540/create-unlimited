package dev.rdh.createunlimited.mixin;

import com.simibubi.create.content.curiosities.tools.ExtendoGripItem;
import dev.rdh.createunlimited.CreateUnlimited;
import dev.rdh.createunlimited.config.CUConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * This mixin is responsible for changing the range of the Extendo-Grip. It's currently a bit broken (meaning completely broken but it doesn't crash)
 */
@Mixin(ExtendoGripItem.class)
public class ExtendoGripItemMixin {
//    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;<init>(Ljava/util/UUID;Ljava/lang/String;DLnet/minecraft/world/entity/ai/attributes/AttributeModifier$Operation;)V", ordinal = 0, remap = false), index = 2, remap = false)
//    private static double modifySingleExtendoGripRange(double original) {
//        try {
//            return CUConfig.singleExtendoGripRange.get();
//        } catch (Exception e) {
//            CreateUnlimited.LOGGER.warn("Failed to get singleExtendoGripRange!");
//            return original;
//        }
//    }
//    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;<init>(Ljava/util/UUID;Ljava/lang/String;DLnet/minecraft/world/entity/ai/attributes/AttributeModifier$Operation;)V", ordinal = 1, remap = false), index = 2, remap = false)
//    private static double modifyDoubleExtendoGripRange(double original) {
//        try {
//            return CUConfig.doubleExtendoGripRange.get();
//        } catch (Exception e) {
//            CreateUnlimited.LOGGER.warn("Failed to get doubleExtendoGripRange!");
//            return original;
//        }
//    }
}
