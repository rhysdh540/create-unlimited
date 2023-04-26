package net.rdh.createunlimited.mixin;

import com.simibubi.create.content.contraptions.components.structureMovement.glue.SuperGlueSelectionHandler;
import net.minecraft.core.BlockPos;
import net.rdh.createunlimited.config.CUConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import java.util.Set;

@Mixin(SuperGlueSelectionHandler.class)
public class SuperGlueSectionHandlerMixin {
    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = 24), remap = false)
    private double modifyMaxSuperGlueDistance(double original) {
        return CUConfig.SERVER.glue.maxGlueConnectionRange.get();
    }
    // shows as working but glue doesn't connect
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"), remap = false)
    private boolean modifyNeedsConnected(Set<BlockPos> instance, Object o) {
        return instance.contains((BlockPos) o) || !CUConfig.SERVER.glue.blocksMustBeConnectedForGlue.get();
    }
    @Redirect(method = "onMouseInput", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"), remap = false)
    private boolean modifyNeedsConnectedClick(Set<BlockPos> instance, Object o) {
        return instance.contains((BlockPos) o) || !CUConfig.SERVER.glue.blocksMustBeConnectedForGlue.get();
    }
}
