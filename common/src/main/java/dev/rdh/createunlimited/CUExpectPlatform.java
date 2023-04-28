package dev.rdh.createunlimited;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class CUExpectPlatform {
    @ExpectPlatform
    public static String platformName() {
        throw new AssertionError();
    }
}
