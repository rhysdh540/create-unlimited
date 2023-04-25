package net.rdh.createunlimited.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class CUServerConfig extends ConfigBase {
    @Override
    public String getName() {
        return "CUConfig";
    }

    public final ConfigBool placementChecksEnabled = b(true, "placementChecksEnabled", Comments.placementChecksEnabled);
    public final ConfigBool veryIllegalDriving = b(true, "veryIllegalDriving", Comments.veryIllegalDriving);
    public final ConfigInt maxTrainRelocatingDistance = i(24, 0, "maxTrainRelocatingDistance", Comments.maxTrainRelocatingDistance);
    public final ConfigInt maxAllowedStress = i(4, -1, "maxAllowedStress", Comments.maxAllowedStress);
    public final ConfigInt maxGlueConnectionRange = i(24, 0, "maxGlueConnectionRange", Comments.maxGlueConnectionRange);
    public final ConfigBool blocksMustBeConnectedForGlue = b(true, "blocksMustBeConnectedForGlue", Comments.blocksMustBeConnectedForGlue);

    private static class Comments {
        public static final String placementChecksEnabled = "Check for illegal track placement.";
        public static final String veryIllegalDriving = "Allow trains to drive on \"very illegal\" tracks.";
        public static final String maxTrainRelocatingDistance = "Maximum distance a train can be relocated using the wrench.";
        public static final String maxAllowedStress = "Maximum stress from couplings before train derails. Set to -1 to disable.";
        public static final String maxGlueConnectionRange = "Maximum distance between two blocks to be considered for glue connections.";
        public static final String blocksMustBeConnectedForGlue = "Require blocks to be connected for glue connections.";
    }
}
