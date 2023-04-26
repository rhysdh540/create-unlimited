package net.rdh.createunlimited.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class CUGlueConfig extends ConfigBase {
    @Override
    public String getName() {
        return "glueSettings";
    }

    public final ConfigInt maxGlueConnectionRange = i(24, 0, "maxGlueConnectionRange", maxGlueConnectionRangeComment);
    public final ConfigBool blocksMustBeConnectedForGlue = b(true, "blocksMustBeConnectedForGlue", blocksMustBeConnectedForGlueComment);

    public static final String maxGlueConnectionRangeComment = "Maximum distance between two blocks to be considered for glue connections.";
    public static final String blocksMustBeConnectedForGlueComment = "Require blocks to be connected for glue connections.";
}
