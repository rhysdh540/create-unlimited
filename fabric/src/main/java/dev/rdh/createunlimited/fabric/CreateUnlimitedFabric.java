package dev.rdh.createunlimited.fabric;

import dev.rdh.createunlimited.CreateUnlimited;

import dev.rdh.createunlimited.command.CUCommands;
import dev.rdh.createunlimited.config.CUConfigs;

import manifold.rt.api.NoBootstrap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;

import net.minecraftforge.fml.config.ModConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.function.Consumer;

import static dev.rdh.createunlimited.multiversion.SupportedMinecraftVersion.*;

@NoBootstrap
public class CreateUnlimitedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
		registerConfigEvents();
        CreateUnlimited.init();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
			CUCommands.register(dispatcher, environment.includeDedicated));
    }


	/**
	 * @param <L> The type of the loading lambda
	 * @param <R> The type of the reloading lambda
	 * @apiNote no they are not left and right
	 */
	@SuppressWarnings("unchecked")
	private static <L, R> void registerConfigEvents() {
		Class<?> modConfigEventsClass = getModConfigEventsClass();

		try {
			Event<L> loadingEvent = (Event<L>) modConfigEventsClass.getMethod("loading", String.class).invoke(null, CreateUnlimited.ID);
			L loading = createHandlerProxy(CUConfigs::onLoad, modConfigEventsClass, "Loading", "onModConfigLoading");
			loadingEvent.register(loading);
		} catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			throw unchecked(e);
		}

		try {
			Event<R> reloadingEvent = (Event<R>) modConfigEventsClass.getMethod("reloading", String.class).invoke(null, CreateUnlimited.ID);
			R reloading = createHandlerProxy(CUConfigs::onReload, modConfigEventsClass, "Reloading", "onModConfigReloading");
			reloadingEvent.register(reloading);
		} catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			throw unchecked(e);
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
			if(v1_19_2 >= CURRENT) {
				return Class.forName("net.minecraftforge.api.fml.event.config.ModConfigEvents");
			} else if(v1_20_1 <= CURRENT) {
				return Class.forName("fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents");
			} else {
				throw new IllegalStateException("Unsupported Minecraft version: " + CURRENT);
			}
		} catch (ClassNotFoundException e) {
			throw unchecked(e);
		}
	}
}
