package dev.rdh.createunlimited.mixin;

import com.simibubi.create.content.logistics.trains.entity.Train;
import dev.rdh.createunlimited.config.CUConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Mixin to modify the maximum stress tha can be placed on the couplings of a train.
 */
@Mixin(Train.class)
public class TrainMixin {
    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = 4), remap = false)
    private double modifyMaxStress(double original) {
        double a = CUConfig.maxAllowedStress.get();
        return (a == -1) ? Double.MAX_VALUE : a;
    }
}
