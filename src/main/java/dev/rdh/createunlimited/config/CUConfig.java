package dev.rdh.createunlimited.config;

import net.createmod.catnip.config.ConfigBase;
import net.createmod.catnip.config.ui.BaseConfigScreen;

import dev.rdh.createunlimited.Util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import dev.rdh.createunlimited.CreateUnlimited;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.config.ModConfig;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static net.minecraftforge.fml.config.ModConfig.Type.*;

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
	public final ConfigInt singleExtendoGripRange = i(3, 0, "singleExtendoGripRange", Comments.singleExtendoGripRange);
	public final ConfigInt doubleExtendoGripRange = i(5, 0, "doubleExtendoGripRange", Comments.doubleExtendoGripRange);


	public final ConfigGroup misc = group(1, "misc", Comments.misc);
	public final ConfigBool chainConveyorConnectionLimits = b(true, "chainConveyorConnectionLimits", Comments.chainConveyorConnectionLimits);
	public final ConfigBool allowAllCopycatBlocks = b(false, "allowAllCopycatBlocks", Comments.allowAllCopycatBlocks);

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
			chainConveyorConnectionLimits = "Whether to check for valid connections when connecting chain conveyors.";

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

	public static String getComment(String name) {
		return Comments.comments.getOrDefault(name, "No comment.");
	}

	public static final CUConfig instance = new CUConfig();

	public static void register() {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		instance.registerAll(builder);
		Util.INSTANCE.registerConfig(SERVER, instance.specification = builder.build());
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

	public static BaseConfigScreen createConfigScreen(@Nullable @SuppressWarnings("unused") Minecraft mc, Screen parent) {
		return createConfigScreen(parent);
	}

	public static <V, T extends ConfigValue<V>> V getOrDefault(CValue<V, T> value, V orElse) {
		try {
			return value.get();
		} catch (IllegalStateException e) {
			if(e.getMessage().contains("config")) {
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