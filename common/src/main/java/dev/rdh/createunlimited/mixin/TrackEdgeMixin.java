package dev.rdh.createunlimited.mixin;

import com.simibubi.create.content.trains.graph.TrackEdge;
import dev.rdh.createunlimited.config.CUConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

/**
 * This mixin is used to modify the {@link TrackEdge#canTravelTo(TrackEdge)} method to allow for "extended driving". Setting the value below 0.1 causes some weird issues where the train turns around at forks, hence the 0.1 minimum.
 * <p>
 * Theoretically, some turns won't work as their connection value is less than 0.1, but in my tests, even 90° straight-up turns worked "fine".
 * <p>
 * If you manage to make a legitimate track that doesn't work with this, please let me know.
 * @see <a href="https://github/rhysdh540/create-unlimited/issues/1">yell at me here</a>
 */
@Mixin(TrackEdge.class)
public class TrackEdgeMixin {
	@ModifyConstant(method = "canTravelTo", constant = @Constant(doubleValue = 0.875), remap = false)
	private double canTravelTo(double original) {
		return CUConfig.extendedDriving.get() ? 0.1 : 0.875;
	}
}
