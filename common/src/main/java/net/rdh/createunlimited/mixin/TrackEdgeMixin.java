package net.rdh.createunlimited.mixin;

import com.simibubi.create.content.logistics.trains.TrackEdge;
import net.rdh.createunlimited.config.CUConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TrackEdge.class)
public class TrackEdgeMixin {
    @Inject(at = @At("HEAD"), method = "canTravelTo", cancellable = true, remap = false)
    private void canTravelTo(TrackEdge other, CallbackInfoReturnable<Boolean> cir) {
        if(CUConfig.SERVER.trains.veryIllegalDriving.get()) {
            cir.setReturnValue(true);
        }
    }
}
