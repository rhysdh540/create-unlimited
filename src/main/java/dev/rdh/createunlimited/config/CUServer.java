package dev.rdh.createunlimited.config;

import com.simibubi.create.foundation.config.ConfigBase;

import dev.rdh.createunlimited.CreateUnlimited;

import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;

import static com.google.common.base.Predicates.*;

@SuppressWarnings("unused") // groups are used as markers for the screen and command
public class CUServer extends ConfigBase {
	@Override
	public String getName() {
		return "server";
	}

	public final ConfigGroup trains = group(1, "trains", Comments.trains);
	public final ConfigEnum<PlacementCheck> placementChecks = e(PlacementCheck.ON, "placementChecks", Comments.placementChecks);
	public final ConfigFloat extendedDriving = f(0.875F, 0F, 0.875F, "extendedDriving", Comments.extendedDriving);
	public final ConfigFloat maxTrainRelocationDistance = f(24F, 0F, "maxTrainRelocationDistance", Comments.maxTrainRelocationDistance);
	public final ConfigFloat maxAllowedStress = f(0.5F, -1F, "maxAllowedStress", Comments.maxAllowedStress);
	public final ConfigBool trainAssemblyChecks = b(true, "trainAssemblyChecks", Comments.trainAssemblyChecks);
	public final ConfigFloat maxTrackBlockPlacingDistance = f(16F, 0F, "maxTrackBlockPlacingDistance", Comments.maxTrackBlockPlacingDistance);

	public final ConfigGroup glue = group(1, "glue", Comments.glue);
	public final ConfigFloat maxGlueConnectionRange = f(24F, 0F, "maxGlueConnectionRange", Comments.maxGlueConnectionRange);
//	public final ConfigBool physicalBlockConnection = b(true, "physicalBlockConnection", Comments.physicalBlockConnection);

	public final ConfigGroup extendo = group(1, "extendo", Comments.extendo);
	public final ConfigInt singleExtendoGripRange = i(3, 0, "singleExtendoGripRange", Comments.singleExtendoGripRange);
	public final ConfigInt doubleExtendoGripRange = i(5, 0, "doubleExtendoGripRange", Comments.doubleExtendoGripRange);


	public final ConfigGroup misc = group(1, "misc", Comments.misc);
	public final ConfigBool allowAllCopycatBlocks = b(false, "allowAllCopycatBlocks", Comments.allowAllCopycatBlocks);

	private static class Comments {
		static String trains = "Realism, what's that?",
					  placementChecks = "Whether to check for valid placement when placing train tracks",
					  extendedDriving = "The minimum turn that trains can drive on. Set to 0.01 if buggy.",
					  maxTrainRelocationDistance = "Maximum distance a train can be relocated using the wrench.",
					  maxAllowedStress = "Maximum stress from couplings before train derails. Set to -1 to disable stress completely.",
					  trainAssemblyChecks = "Whether to check for valid assembly when placing train tracks",
					  maxTrackBlockPlacingDistance = "Maximum distance a track-targeting block can be placed away from a track.";

		static String glue = "Stick anything together!",
					  maxGlueConnectionRange = "Maximum distance between two blocks for them to be considered for glue connections.",
					  physicalBlockConnection = "Require blocks to be connected for glue connections.";

		static String extendo = "Extend even more!",
					  singleExtendoGripRange = "How much to extend your reach when holding an Extendo-Grip. Adds to your base reach.",
					  doubleExtendoGripRange = "How much to extend your reach when holding two Extendo-Grips. Adds to your base reach.";

		static String misc = "Everything else",
					  allowAllCopycatBlocks = "Whether or not to allow all blocks to be inserted into Copycat blocks.";
	}

	public static String getComment(String name) {
		try {
			return (String) Comments.class.getDeclaredField(name).get(null);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			CreateUnlimited.LOGGER.error("Failed to get comment for " + name, e);
			return "No comment.";
		}
	}

	public enum PlacementCheck {
		ON(alwaysTrue()),
		SURVIVAL_ONLY(not(Player::isCreative)),
		OFF(alwaysFalse());

		final Predicate<Player> enabled;

		PlacementCheck(Predicate<Player> enabled) {
			this.enabled = enabled;
		}
		public boolean isEnabledFor(Player player) {
			return enabled.test(player);
		}
	}
}
