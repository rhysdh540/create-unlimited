package dev.rdh.createunlimited.mixin;

import com.simibubi.create.content.logistics.trains.entity.TrainRelocator;
import dev.rdh.createunlimited.config.CUConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Mixin to modify the maximum distance a train can be relocated with a wrench.
 */
@Mixin(TrainRelocator.class)
public class TrainRelocatorMixin {
    @ModifyConstant(method = "onClicked", constant = @Constant(doubleValue = 24), remap = false)
    private static double modifyMaxTrainRelocatingDistance(double original) {
        return CUConfig.maxTrainRelocatingDistance.get();
    }
    @ModifyConstant(method = "clientTick", constant = @Constant(doubleValue = 24), remap = false)
    private static double modifyMaxTrainRelocatingDistanceClient(double original) {
        return CUConfig.maxTrainRelocatingDistance.get();
    }
}
