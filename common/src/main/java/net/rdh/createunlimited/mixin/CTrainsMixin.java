package net.rdh.createunlimited.mixin;

import com.simibubi.create.foundation.config.CTrains;
import com.simibubi.create.foundation.config.ConfigBase;
import net.rdh.createunlimited.CreateUnlimited;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CTrains.class)
public class CTrainsMixin extends ConfigBase {
    static {
        CreateUnlimited.LOGGER.info("CTrains override loaded (probably)");
    }
    @Override public String getName() {
        return "trains";
    }

    public final ConfigInt maxTrackPlacementLength = i(32, 16, "maxTrackPlacementLength", "Maximum length of track that can be placed as one batch or turn.");
}
