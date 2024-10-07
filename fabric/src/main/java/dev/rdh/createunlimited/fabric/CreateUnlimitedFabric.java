package dev.rdh.createunlimited.fabric;

import dev.rdh.createunlimited.CreateUnlimited;

import dev.rdh.createunlimited.command.CUCommands;
import dev.rdh.createunlimited.config.CUConfig;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;

import net.minecraftforge.fml.config.ModConfig;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

import static dev.rdh.createunlimited.multiversion.SupportedMinecraftVersion.*;

public class CreateUnlimitedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
		registerConfigEvents();
        CreateUnlimited.init();

		CommandRegistrationCallback.EVENT.register(CUCommands::register);
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
			L loading = createHandlerProxy(CUConfig::onLoad, modConfigEventsClass, "Loading", "onModConfigLoading");
			loadingEvent.register(loading);
		} catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			throw unchecked(e);
		}

		try {
			Event<R> reloadingEvent = (Event<R>) modConfigEventsClass.getMethod("reloading", String.class).invoke(null, CreateUnlimited.ID);
			R reloading = createHandlerProxy(CUConfig::onReload, modConfigEventsClass, "Reloading", "onModConfigReloading");
			reloadingEvent.register(reloading);
		} catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			throw unchecked(e);
		}
	}

	private static <T> T createHandlerProxy(Consumer<ModConfig> handler, Class<?> modConfigEventsClass, String handlerTypeName, String handlerMethodName) {
		try {
			@SuppressWarnings("unchecked")
			Class<T> clazz = (Class<T>) findNestedClass(modConfigEventsClass, handlerTypeName);
			MethodHandle handle = MethodHandles.publicLookup()
				.findStatic(clazz, handlerMethodName, MethodType.methodType(void.class, ModConfig.class));
			return MethodHandleProxies.asInterfaceInstance(clazz, handle.bindTo(handler));
		} catch (Throwable t) {
			throw unchecked(t);
		}
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
