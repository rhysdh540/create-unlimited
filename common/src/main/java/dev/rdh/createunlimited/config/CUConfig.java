package dev.rdh.createunlimited.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import dev.rdh.createunlimited.CreateUnlimited;
import org.jetbrains.annotations.NotNull;

public class CUConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    //trains
    public static ForgeConfigSpec.BooleanValue placementChecksEnabled;
    public static ForgeConfigSpec.BooleanValue veryIllegalDriving;
    public static ForgeConfigSpec.IntValue maxTrainRelocatingDistance;
    public static ForgeConfigSpec.DoubleValue maxAllowedStress;

    //glue
    public static ForgeConfigSpec.IntValue maxGlueConnectionRange;
    public static ForgeConfigSpec.BooleanValue blocksMustBeConnectedForConnection;

    //extendo grip
    public static ForgeConfigSpec.IntValue singleExtendoGripRange;
    public static ForgeConfigSpec.IntValue doubleExtendoGripRange;

    static {
        BUILDER.comment("Create Unlimited Config").push("CreateUnlimited");

        BUILDER.comment("Train Settings").push("Trains");
        placementChecksEnabled = b(true, "placementChecksEnabled", "Whether or not to enable the placement checks for train tracks.");
        veryIllegalDriving = b(false, "veryIllegalDriving", "Whether or not to allow trains to drive on \"very illegal\" tracks. Slightly buggy.");
        maxTrainRelocatingDistance = i(24, 0, "maxTrainRelocatingDistance", "Maximum distance a train can be relocated using the wrench.");
        maxAllowedStress = d(4.0, -1.0, "maxAllowedStress", "Maximum stress from couplings before train derails. Set to -1 to disable.");

        BUILDER.pop().comment("Glue Settings").push("Glue");
        maxGlueConnectionRange = i(24, 0, "maxGlueConnectionRange", "Maximum distance between two blocks for them to be considered for glue connections.");
        blocksMustBeConnectedForConnection = b(true, "blocksMustBeConnectedForConnection", "Require blocks to be connected for glue connections.");

        BUILDER.pop().comment("Extendo Grip Settings").push("ExtendoGrip");
        singleExtendoGripRange = i(3, 0, "singleExtendoGripRange", "How much to extend your reach when holding an Extendo-Grip. Adds to your base reach.");
        doubleExtendoGripRange = i(5, 0, "doubleExtendoGripRange", "How much to extend your reach when holding two Extendo-Grips. Adds to your base reach.");
        BUILDER.pop(2);

        SPEC = BUILDER.build();
    }

    public static void init(@NotNull ForgeConfigSpec spec, java.nio.file.Path path) {
        CreateUnlimited.LOGGER.info("Loading CU config!");


        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();
        spec.setConfig(configData);
    }

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
