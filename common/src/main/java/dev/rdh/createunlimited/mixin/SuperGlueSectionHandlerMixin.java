package dev.rdh.createunlimited.mixin;

import com.simibubi.create.content.contraptions.components.structureMovement.glue.SuperGlueSelectionHandler;
import net.minecraft.core.BlockPos;
import dev.rdh.createunlimited.config.CUConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import java.util.Set;

@Mixin(SuperGlueSelectionHandler.class)
public class SuperGlueSectionHandlerMixin {
    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = 24), remap = false)
    private double modifyMaxSuperGlueDistance(double original) {
        return CUConfig.maxGlueConnectionRange.get();
    }
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"), remap = false)
    private boolean modifyNeedsConnected(Set<BlockPos> instance, Object o) {
        return instance.contains((BlockPos) o) || !CUConfig.blocksMustBeConnectedForConnection.get();
    }
    @Redirect(method = "onMouseInput", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"), remap = false)
    private boolean modifyNeedsConnectedClick(Set<BlockPos> instance, Object o) {
        return instance.contains((BlockPos) o) || !CUConfig.blocksMustBeConnectedForConnection.get();
    }
}
