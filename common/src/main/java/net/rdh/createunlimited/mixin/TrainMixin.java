package net.rdh.createunlimited.mixin;

import com.simibubi.create.content.logistics.trains.entity.Train;
import net.rdh.createunlimited.config.CUConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Train.class)
public class TrainMixin {
    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = 4), remap = false)
    private double modifyMaxStress(double original) {
        return CUConfig.SERVER.trains.maxAllowedStress.get();
    }
}
