package dev.rdh.createunlimited.forge;

import dev.rdh.createunlimited.CreateUnlimited;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import static dev.rdh.createunlimited.multiversion.SupportedMinecraftVersion.*;

@Mod(CreateUnlimited.ID)
public final class CreateUnlimitedForge {

	static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENTS = DeferredRegister.create(getCommandArgumentTypeRegistry(), CreateUnlimited.ID);

    public CreateUnlimitedForge() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get()
			.getModEventBus();
		IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

		ARGUMENTS.register(modEventBus);
		forgeEventBus.register(Events.ClientModBusEvents.class);
		forgeEventBus.register(Events.class);
		modEventBus.addListener(Events.ClientModBusEvents::onLoadComplete);
		CreateUnlimited.init();
    }

	@SuppressWarnings({"unchecked", "JavaReflectionMemberAccess", "RedundantSuppression"})
	private static ResourceKey<Registry<ArgumentTypeInfo<?, ?>>> getCommandArgumentTypeRegistry() {
		try {
			if(v1_19_2 >= CURRENT) {
				return (ResourceKey<Registry<ArgumentTypeInfo<?, ?>>>)
					Registry.class.getDeclaredField("COMMAND_ARGUMENT_TYPE_REGISTRY").get(null);
			} else if(v1_20_1 <= CURRENT) {
				return (ResourceKey<Registry<ArgumentTypeInfo<?, ?>>>)
					Class.forName("net.minecraft.core.registries.Registries").getDeclaredField("COMMAND_ARGUMENT_TYPE").get(null);
			} else {
				throw new IllegalStateException("Unsupported Minecraft version: " + CURRENT);
			}
		} catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
			throw unchecked(e);
		}
	}
}
