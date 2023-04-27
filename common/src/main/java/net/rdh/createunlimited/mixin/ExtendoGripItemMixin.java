package net.rdh.createunlimited.mixin;

import com.simibubi.create.content.curiosities.tools.ExtendoGripItem;
import net.rdh.createunlimited.CreateUnlimited;
import net.rdh.createunlimited.config.CUConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ExtendoGripItem.class)
public class ExtendoGripItemMixin {
    //TODO: currently not working, this mixes in and then the static initializer calls .get(), but the config isn't initalized yet so it throws an IllegalStateException
//    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;<init>(Ljava/util/UUID;Ljava/lang/String;DLnet/minecraft/world/entity/ai/attributes/AttributeModifier$Operation;)V", ordinal = 0), index = 2)
//    private static double modifySingleExtendoGripRange(double original) {
//        try {
//            return CUConfig.singleExtendoGripRange.get();
//        } catch (IllegalStateException e) {
//            CreateUnlimited.LOGGER.warn("Failed to get singleExtendoGripRange!");
//            return original;
//        }
//    }
//    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;<init>(Ljava/util/UUID;Ljava/lang/String;DLnet/minecraft/world/entity/ai/attributes/AttributeModifier$Operation;)V", ordinal = 1), index = 2)
//    private static double modifyDoubleExtendoGripRange(double original) {
//        try {
//            return CUConfig.doubleExtendoGripRange.get();
//        } catch (IllegalStateException e) {
//            CreateUnlimited.LOGGER.warn("Failed to get doubleExtendoGripRange!");
//            return original;
//        }
//    }
}
