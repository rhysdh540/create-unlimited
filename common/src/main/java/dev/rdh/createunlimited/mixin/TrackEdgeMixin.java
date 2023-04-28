package dev.rdh.createunlimited.mixin;

import com.simibubi.create.content.logistics.trains.TrackEdge;
import dev.rdh.createunlimited.config.CUConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(TrackEdge.class)
public class TrackEdgeMixin {
    @ModifyConstant(method = "canTravelTo", constant = @Constant(doubleValue = 0.875), remap = false)
    private double canTravelTo(double original) {
        return CUConfig.veryIllegalDriving.get() ? 0.1 : 0.875;
    }
}
