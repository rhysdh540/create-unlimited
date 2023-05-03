package dev.rdh.createunlimited.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;

import dev.rdh.createunlimited.CreateUnlimited;

import java.nio.file.Path;

/**
 * The configuration class for Create Unlimited.
 * <p>
 * This class is responsible for declaring, initializing, and registering the mod's configuration.
 * Since it uses the Forge Configuration Specification, an external mod (Forge Config API Port) is required on Fabric, but it's probably bundled with the mod. (or some weird witchery means that it works anyway idk)
 * <p>
 * The configuration is stored in {@code [world folder]/serverconfig/createunlimited.toml}.
 *
 * @see <a href="https://github.com/TheElectronWill/night-config">Night Config on GitHub</a>
 */
public class CUConfig {
    public static final ForgeConfigSpec SPEC;
    public static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public enum PlacementCheck {
        ON,
        SURVIVAL_ONLY,
        OFF,
    }
    public static String train;
    public static ForgeConfigSpec.EnumValue<PlacementCheck> placementChecksEnabled;
    public static ForgeConfigSpec.BooleanValue extendedDriving;
    public static ForgeConfigSpec.IntValue maxTrainRelocatingDistance;
    public static ForgeConfigSpec.DoubleValue maxAllowedStress;

    public static String glue;
    public static ForgeConfigSpec.IntValue maxGlueConnectionRange;
    public static ForgeConfigSpec.BooleanValue blocksMustBeConnectedForConnection;

    public static String extendo;
    public static ForgeConfigSpec.IntValue singleExtendoGripRange;
    public static ForgeConfigSpec.IntValue doubleExtendoGripRange;

    /* cant do javadoc on static initializers :(((
     * but basically this builds the config and gives everything a default value and range
     */
    static {
        BUILDER.comment("Create Unlimited Config").push("CreateUnlimited");

        BUILDER.comment("Train Settings").push("Trains");
        placementChecksEnabled = BUILDER.comment("Whether or not to enable the placement checks for train tracks.").defineEnum("placementChecksEnabled", PlacementCheck.ON);
        extendedDriving = b(false, "extendedDriving", "Whether or not to allow trains to drive on \"very illegal\" tracks. Slightly buggy.");
        maxTrainRelocatingDistance = i(24, 0, "maxTrainRelocatingDistance", "Maximum distance a train can be relocated using the wrench.");
        maxAllowedStress = d(4.0, -1.0, "maxAllowedStress", "Maximum stress from couplings before train derails. Set to -1 to disable.");

        BUILDER.pop().comment("Glue Settings").push("SuperGlue");
        maxGlueConnectionRange = i(24, 0, "maxGlueConnectionRange", "Maximum distance between two blocks for them to be considered for glue connections.");
        blocksMustBeConnectedForConnection = b(true, "blocksMustBeConnectedForConnection", "Require blocks to be connected for glue connections.");

        BUILDER.pop().comment("Extendo Grip Settings").push("ExtendoGrip");
        singleExtendoGripRange = i(3, 0, "singleExtendoGripRange", "How much to extend your reach when holding an Extendo-Grip. Adds to your base reach.");
        doubleExtendoGripRange = i(5, 0, "doubleExtendoGripRange", "How much to extend your reach when holding two Extendo-Grips. Adds to your base reach.");
        BUILDER.pop(2);

        SPEC = BUILDER.build();
    }

    /**
     * Initializes and registers the config.
     * @param path The path to the config file.
     */
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

    // helper methods for declaring config values
    public static ForgeConfigSpec.BooleanValue b(boolean normal, String path, String comment) {
        return BUILDER.comment(comment).define(path, normal);
    }
    public static ForgeConfigSpec.IntValue i(int normal, int min, String path, String comment) {
        return BUILDER.comment(comment).defineInRange(path, normal, min, Integer.MAX_VALUE);
    }
    public static ForgeConfigSpec.DoubleValue d(double normal, double min, String path, String comment) {
        return BUILDER.comment(comment).defineInRange(path, normal, min, Double.MAX_VALUE);
    }
}
