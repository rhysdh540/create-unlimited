package net.rdh.createunlimited.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class CUServerConfig extends ConfigBase {
    @Override
    public String getName() {
        return "server";
    }

    public final CUTrainsConfig trains = nested(0, CUTrainsConfig::new, "Settings for train limits");
    public final CUGlueConfig glue = nested(0, CUGlueConfig::new, "Settings for glue placing");
    public final CUExtendoConfig extendo = nested(0, CUExtendoConfig::new, "Settings for Extendo-Grip reach");
}
