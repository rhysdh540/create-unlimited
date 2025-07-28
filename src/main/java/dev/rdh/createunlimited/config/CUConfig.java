package dev.rdh.createunlimited.config;

import net.createmod.catnip.config.ui.BaseConfigScreen;

import dev.rdh.createunlimited.Util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import dev.rdh.createunlimited.CreateUnlimited;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.createmod.catnip.config.ConfigBase;

#if MC >= 21
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
#else
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
#endif

@SuppressWarnings("unused")
public class CUConfig extends ConfigBase {

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
	public final ConfigFloat singleExtendoGripRange = extendo(3, 0, "singleExtendoGripRange", Comments.singleExtendoGripRange);
	public final ConfigFloat doubleExtendoGripRange = extendo(5, 0, "doubleExtendoGripRange", Comments.doubleExtendoGripRange);

	public final ConfigGroup misc = group(1, "misc", Comments.misc);
	public final ConfigBool chainConveyorConnectionLimits = b(true, "chainConveyorConnectionLimits", Comments.chainConveyorConnectionLimits);
	public final ConfigBool allowAllCopycatBlocks = b(false, "allowAllCopycatBlocks", Comments.allowAllCopycatBlocks);
	public final ConfigBool allowContraptionMoveAllow = b(false, "allowContraptionMoveAllow", Comments.allowContraptionMoveAllow);

	private static class Comments {
		static final String trains = "Realism, what's that?",
			placementChecks = "Whether to check for valid placement when placing train tracks",
			extendedDriving = "The minimum turn that trains can drive on. Set to 0.01 if buggy.",
			maxTrainRelocationDistance = "Maximum distance a train can be relocated using the wrench.",
			maxAllowedStress = "Maximum stress from couplings before train derails. Set to -1 to disable stress completely.",
			trainAssemblyChecks = "Whether to check for valid assembly when placing train tracks",
			maxTrackBlockPlacingDistance = "Maximum distance a track-targeting block can be placed away from a track.";

		static final String glue = "Stick anything together!",
			maxGlueConnectionRange = "Maximum distance between two blocks for them to be considered for glue connections.",
			physicalBlockConnection = "Require blocks to be connected for glue connections.";

		static final String extendo = "Extend even more!",
			singleExtendoGripRange = "How much to extend your reach when holding an Extendo-Grip. Adds to your base reach.",
			doubleExtendoGripRange = "How much to extend your reach when holding two Extendo-Grips. Adds to your base reach.";

		static final String misc = "Everything else",
			allowAllCopycatBlocks = "Whether or not to allow all blocks to be inserted into Copycat blocks.",
			chainConveyorConnectionLimits = "Whether to check for valid connections when connecting chain conveyors.",
			allowContraptionMoveAllow = "Whether to allow contraptions to move any block.";

		private static final Map<String, String> comments = new HashMap<>();
		static {
			for(Field field : Comments.class.getDeclaredFields()) {
				try {
					comments.put(field.getName(), (String) field.get(null));
				} catch (IllegalAccessException e) {
					CreateUnlimited.LOGGER.error("Failed to get comment for " + field.getName(), e);
				}
			}
		}
	}

	private ExtendoRange extendo(float current, float min, String name, String... comment) {
		return new ExtendoRange(name, current, min, Float.MAX_VALUE, comment);
	}

	private class ExtendoRange extends ConfigFloat {
		public ExtendoRange(String name, float current, float min, float max, String... comment) {
			super(name, current, min, max, comment);
		}

		@Override
		public void set(Double value) {
			// TODO: notify players holding extendos that this got changed
			super.set(value);
		}
	}

	public static String getComment(String name) {
		return Comments.comments.getOrDefault(name, "No comment.");
	}

	public static final CUConfig instance = new CUConfig();

	public static void register() {
		Builder builder = new Builder();
		instance.registerAll(builder);
		Util.registerConfig(Type.SERVER, instance.specification = builder.build());
	}

	public static void onLoad(ModConfig modConfig) {
		if (instance.specification == modConfig.getSpec())
			instance.onLoad();
	}

	public static void onReload(ModConfig modConfig) {
		if(instance.specification == modConfig.getSpec())
			instance.onReload();
	}

	public static BaseConfigScreen createConfigScreen(Screen parent) {
		initBCS();
		return new BaseConfigScreen(parent, CreateUnlimited.ID);
	}

	private static boolean done = false;

	private static void initBCS() {
		if(done) return;
		BaseConfigScreen.setDefaultActionFor(CreateUnlimited.ID, base ->
			base.withSpecs(null, null, instance.specification)
				.withButtonLabels("", "", "Settings")
		);
		done = true;
	}

	public static <V, T extends ConfigValue<V>> V getOrDefault(CValue<V, T> value, V orElse) {
		try {
			return value.get();
		} catch (IllegalStateException | AssertionError e) {
			if(e.getMessage().toLowerCase(Locale.ROOT).contains("config")) {
				return orElse;
			}
			throw e;
		}
	}

	public static boolean getOrFalse(ConfigBool config) {
		return getOrDefault(config, false);
	}

	public static boolean getOrTrue(ConfigBool config) {
		return getOrDefault(config, true);
	}
}