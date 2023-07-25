package net.examplemod.forge;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;

public class PreLaunchSetup {
    public static void onPreLaunch() {
        MixinExtrasBootstrap.init();
    }
}
