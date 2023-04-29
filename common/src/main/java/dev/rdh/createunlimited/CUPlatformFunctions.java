package dev.rdh.createunlimited;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

public class CUPlatformFunctions {
    @ExpectPlatform
    public static String platformName() {
        throw new AssertionError();
    }
    @ExpectPlatform
    public static Path getConfigDirectory() {
        throw new AssertionError();
    }
    @ExpectPlatform
    public static boolean isClientEnv() {
        throw new AssertionError();
    }
    @ExpectPlatform
    public static boolean isModLoaded(String modid) {
        throw new AssertionError();
    }
}
