package net.rdh.createunlimited;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final String CATEGORY_CU = "createunlimited";

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.BooleanValue PLACEMENT_ENABLED;
    public static ForgeConfigSpec.IntValue TRACK_RANGE;
    public static ForgeConfigSpec.BooleanValue VERY_ILLEGAL_DRIVING;
    public static ForgeConfigSpec.IntValue MAX_TRAIN_RELOCATING_DISTANCE;
    public static ForgeConfigSpec.IntValue MAX_ALLOWED_STRESS;
    public static ForgeConfigSpec.IntValue MAX_GLUE_RANGE;

    static {
        BUILDER.comment("Enable/Disable CU Features").push(CATEGORY_CU);
        PLACEMENT_ENABLED = BUILDER.comment("Allow for the placement of illegal tracks.").define("placement_enabled", true);
        TRACK_RANGE = BUILDER.comment("Maximum range for track placement. This overrides Create's config value").defineInRange("track_range", 24, 0, Integer.MAX_VALUE);
        VERY_ILLEGAL_DRIVING = BUILDER.comment("Allow trains to drive on \"very illegal\" tracks.").define("very_illegal_driving", true);
        MAX_TRAIN_RELOCATING_DISTANCE = BUILDER.comment("Allow for the use of the wrench.").defineInRange("max_train_relocating_distance", 24, 0, 128);
        MAX_ALLOWED_STRESS = BUILDER.comment("Maximum stress from couplings before train derails. Set to -1 to disable.").defineInRange("max_allowed_stress", 4, -1, 128);
        MAX_GLUE_RANGE = BUILDER.comment("Maximum range for superglue.").defineInRange("max_glue_range", 24, 0, 256);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
    public static void loadConfig(ForgeConfigSpec spec, java.nio.file.Path path) {
        CreateUnlimited.LOGGER.info("Loading CU config!");
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();
        spec.setConfig(configData);
    }
}
