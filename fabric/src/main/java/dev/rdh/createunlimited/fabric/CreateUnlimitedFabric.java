package dev.rdh.createunlimited.fabric;

import dev.rdh.createunlimited.CreateUnlimited;

import dev.rdh.createunlimited.Util;
import dev.rdh.createunlimited.command.CUCommands;
import dev.rdh.createunlimited.config.CUConfig;

import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;

import net.minecraftforge.fml.config.ModConfig;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

import static dev.rdh.createunlimited.multiversion.SupportedMinecraftVersion.*;

public class CreateUnlimitedFabric implements CreateUnlimited, ModInitializer {
    @Override
    public void onInitialize() {
		ModConfigEvents.loading(CreateUnlimited.ID).register(CUConfig::onLoad);
		ModConfigEvents.reloading(CreateUnlimited.ID).register(CUConfig::onReload);
		CommandRegistrationCallback.EVENT.register(CUCommands::register);

		System.setProperty("createunlimited.util.classname", UtilImpl.class.getName());

		this.init();
	}
}
