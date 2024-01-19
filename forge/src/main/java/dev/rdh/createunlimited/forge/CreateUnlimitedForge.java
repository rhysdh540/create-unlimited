package dev.rdh.createunlimited.forge;

import dev.rdh.createunlimited.CreateUnlimited;

import manifold.rt.api.NoBootstrap;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@NoBootstrap
@Mod(CreateUnlimited.ID)
public final class CreateUnlimitedForge {

    public CreateUnlimitedForge() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get()
			.getModEventBus();
		IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

		forgeEventBus.register(Events.ClientModBusEvents.class);
		forgeEventBus.register(Events.class);
		modEventBus.addListener(Events.ClientModBusEvents::onLoadComplete);
		CreateUnlimited.init();
    }
}
