package net.rdh.createunlimited.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class CUTrainsConfig extends ConfigBase {
    @Override
    public String getName() {
        return "trainSettings";
    }

    public final ConfigBool placementChecksEnabled = b(true, "placementChecksEnabled", placementChecksEnabledComment);
    public final ConfigBool veryIllegalDriving = b(false, "veryIllegalDriving", veryIllegalDrivingComment);
    public final ConfigInt maxTrainRelocatingDistance = i(24, 0, "maxTrainRelocatingDistance", maxTrainRelocatingDistanceComment);
    public final ConfigFloat maxAllowedStress = f(4, -1, "maxAllowedStress", maxAllowedStressComment);

    public static final String placementChecksEnabledComment = "Check for illegal track placement.";
    public static final String veryIllegalDrivingComment = "Allow trains to drive on \"very illegal\" tracks.";
    public static final String maxTrainRelocatingDistanceComment = "Maximum distance a train can be relocated using the wrench.";
    public static final String maxAllowedStressComment = "Maximum stress from couplings before train derails. Set to -1 to disable.";
}
