package dev.rdh.createunlimited.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import com.simibubi.create.foundation.config.ui.BaseConfigScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import net.minecraft.world.entity.player.Player;

import net.minecraftforge.common.ForgeConfigSpec;

import dev.rdh.createunlimited.CreateUnlimited;

import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@SuppressWarnings({"SameParameterValue", "unused"})
public class CUConfig {
	private CUConfig() { throw new UnsupportedOperationException(); }
	public static final ForgeConfigSpec SPEC;
	public static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	public enum PlacementCheck {
		ON(p -> true),
		SURVIVAL_ONLY(p -> !p.isCreative()),
		OFF(p -> false);

		final Predicate<Player> enabled;

		PlacementCheck(Predicate<Player> enabled) {
			this.enabled = enabled;
		}
		public boolean isEnabledFor(Player player) {
			return enabled.test(player);
		}
	}

	public static final Map<String, String> comments = new HashMap<>();
	static {
		comments.put("trains", "Train Settings");
		comments.put("placementChecks", "Whether or not to enable the placement checks for train tracks.");
		comments.put("extendedDriving", "Whether or not to allow trains to drive on tracks with very small turn radii. Slightly buggy.");
		comments.put("extendedDrivingValue", "The minimum turn that trains can drive on. Only works if extendedDriving is enabled.");
		comments.put("maxTrainRelocationDistance", "Maximum distance a train can be relocated using the wrench.");
		comments.put("maxAllowedStress", "Maximum stress from couplings before train derails. Set to -1 to disable.");

		comments.put("glue", "Glue Settings");
		comments.put("maxGlueConnectionRange", "Maximum distance between two blocks for them to be considered for glue connections.");
//		comments.put("physicalBlockConnection", "Require blocks to be connected for glue connections.");

//		comments.put("extendo", "Extendo Grip Settings");
//		comments.put("singleExtendoGripRange", "How much to extend your reach when holding an Extendo-Grip. Adds to your base reach.");
//		comments.put("doubleExtendoGripRange", "How much to extend your reach when holding two Extendo-Grips. Adds to your base reach.");

		comments.put("copycat", "Copycat Settings");
		comments.put("allowAllCopycatBlocks", "Whether or not to allow all blocks to be inserted into Copycat blocks.");
	}

	public static String trains;
	public static ForgeConfigSpec.EnumValue<PlacementCheck> placementChecks;
	public static ForgeConfigSpec.BooleanValue extendedDriving;
	public static ForgeConfigSpec.DoubleValue extendedDrivingValue;
	public static ForgeConfigSpec.IntValue maxTrainRelocationDistance;
	public static ForgeConfigSpec.DoubleValue maxAllowedStress;

	public static String glue;
	public static ForgeConfigSpec.IntValue maxGlueConnectionRange;
//	public static ForgeConfigSpec.BooleanValue physicalBlockConnection; // todo make this work

//	public static String extendo;
//	public static ForgeConfigSpec.IntValue singleExtendoGripRange;
//	public static ForgeConfigSpec.IntValue doubleExtendoGripRange;

	public static String copycat;
	public static ForgeConfigSpec.BooleanValue allowAllCopycatBlocks;

	static {
		BUILDER.comment("Create Unlimited Config").push("CreateUnlimited");

		cat("Trains");
		placementChecks = BUILDER.comment(comments.get("placementChecks")).defineEnum("placementChecks", PlacementCheck.ON);
		extendedDriving = b(false, "extendedDriving");
		extendedDrivingValue = d(0.1, 0.0, 0.875, "extendedDrivingValue");
		maxTrainRelocationDistance = i(24, 0, "maxTrainRelocationDistance");
		maxAllowedStress = d(4.0, -1.0, "maxAllowedStress");

		BUILDER.pop();
		cat("Glue");
		maxGlueConnectionRange = i(24, 0, "maxGlueConnectionRange");
		//physicalBlockConnection = b(true, "physicalBlockConnection");

//		BUILDER.pop().comment(comments.get("extendo")).push("ExtendoGrip");
//		singleExtendoGripRange = i(3, 0, "singleExtendoGripRange", comments.get("singleExtendoGripRange"));
//		doubleExtendoGripRange = i(5, 0, "doubleExtendoGripRange", comments.get("doubleExtendoGripRange"));

		BUILDER.pop();
		cat("Copycat");
		allowAllCopycatBlocks = b(false, "allowAllCopycatBlocks");
		BUILDER.pop(2);

		SPEC = BUILDER.build();
	}

	public static void init(Path path) {
		CreateUnlimited.LOGGER.info("Loading Create Unlimited config!");
		final CommentedFileConfig configData = CommentedFileConfig.builder(path)
			.sync()
			.autosave()
			.writingMode(WritingMode.REPLACE)
			.build();
		configData.load();
		SPEC.setConfig(configData);
	}

	// helper methods for initializing config values
	private static ForgeConfigSpec.BooleanValue b(boolean normal, String path) {
		return BUILDER.comment(comments.get(path)).define(path, normal);
	}

	private static ForgeConfigSpec.IntValue i(int normal, int min, String path) {
		return BUILDER.comment(comments.get(path)).defineInRange(path, normal, min, Integer.MAX_VALUE);
	}

	private static ForgeConfigSpec.DoubleValue d(double normal, double min, String path) {
		return d(normal, min, Double.MAX_VALUE, path);
	}

	private static ForgeConfigSpec.DoubleValue d(double normal, double min, double max, String path) {
		return BUILDER.comment(comments.get(path)).defineInRange(path, normal, min, max);
	}

	private static void cat(String path) {
		BUILDER.comment(comments.get(path.toLowerCase())).push(path);
	}


	public static BaseConfigScreen createConfigScreen(Screen parent) {
		BaseConfigScreen.setDefaultActionFor(CreateUnlimited.ID, (base) ->
			base.withSpecs(null, null, CUConfig.SPEC)
				.withTitles("", "", "Settings")
		);
		return new BaseConfigScreen(parent, CreateUnlimited.ID);
	}
	public static BaseConfigScreen createConfigScreen(@Nullable Minecraft mc, Screen parent) {
		return createConfigScreen(parent);
	}
}