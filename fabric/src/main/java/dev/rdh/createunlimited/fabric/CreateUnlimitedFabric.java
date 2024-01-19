package dev.rdh.createunlimited.fabric;

import dev.rdh.createunlimited.CreateUnlimited;

import dev.rdh.createunlimited.config.CUConfigs;
import dev.rdh.createunlimited.multiversion.SupportedMinecraftVersion;

import manifold.rt.api.NoBootstrap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;

import net.minecraftforge.fml.config.ModConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.function.Consumer;

@NoBootstrap
public class CreateUnlimitedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
		registerConfigEvents();
        CreateUnlimited.init();
    }

	@SuppressWarnings("unchecked")
	private static <Loading, Reloading> void registerConfigEvents() {
		Class<?> modConfigEventsClass = getModConfigEventsClass();

		try {
			Event<Loading> loadingEvent = (Event<Loading>) modConfigEventsClass.getMethod("loading", String.class).invoke(null, CreateUnlimited.ID);
			Loading loading = createHandlerProxy(CUConfigs::onLoad, modConfigEventsClass, "Loading", "onModConfigLoading");
			loadingEvent.register(loading);
		} catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		try {
			Event<Reloading> reloadingEvent = (Event<Reloading>) modConfigEventsClass.getMethod("reloading", String.class).invoke(null, CreateUnlimited.ID);
			Reloading reloading = createHandlerProxy(CUConfigs::onReload, modConfigEventsClass, "Reloading", "onModConfigReloading");
			reloadingEvent.register(reloading);
		} catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings({"unchecked", "SuspiciousInvocationHandlerImplementation"})
	private static <T> T createHandlerProxy(Consumer<ModConfig> handler, Class<?> modConfigEventsClass, String handlerTypeName, String handlerMethodName) {
		return (T) Proxy.newProxyInstance(
			modConfigEventsClass.getClassLoader(),
			new Class<?>[]{findNestedClass(modConfigEventsClass, handlerTypeName)},
			(proxy, method, args) -> {
				if (method.getName().equals(handlerMethodName) && args.length == 1 && args[0] instanceof ModConfig config) {
					handler.accept(config);
					return null;
				}
				throw new UnsupportedOperationException("Unexpected method: " + method);
			});
	}

	private static Class<?> findNestedClass(Class<?> outerClass, String nestedClassName) {
		for (Class<?> nestedClass : outerClass.getDeclaredClasses()) {
			if (nestedClass.getSimpleName().equals(nestedClassName)) {
				return nestedClass;
			}
		}
		throw new IllegalArgumentException("Nested class " + nestedClassName + " not found in " + outerClass.getName());
	}

	private static Class<?> getModConfigEventsClass() {
		try {
			if(SupportedMinecraftVersion.v1_19_2.isCurrentOrOlder()) {
				return Class.forName("net.minecraftforge.api.fml.event.config.ModConfigEvents");
			} else if(SupportedMinecraftVersion.v1_20_1.isCurrentOrNewer()) {
				return Class.forName("fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents");
			} else {
				throw new IllegalStateException("Unsupported Minecraft version: " + SupportedMinecraftVersion.CURRENT);
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
