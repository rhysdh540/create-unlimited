package net.rdh.createunlimited.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class CUExtendoConfig extends ConfigBase {
    @Override
    public String getName() {
        return "extendoGripSettings";
    }

    public final ConfigInt singleExtendoGripRange = i(3, 0, "singleExtendoGripRange", singleExtendoGripRangeComment);
    public final ConfigInt doubleExtendoGripRange = i(5, 0, "doubleExtendoGripRange", doubleExtendoGripRangeComment);

    public static final String singleExtendoGripRangeComment = "Amount of blocks holding one Extendo-Grip extends your reach.";
    public static final String doubleExtendoGripRangeComment = "Amount of blocks holding two Extendo-Grips extends your reach.";
}
