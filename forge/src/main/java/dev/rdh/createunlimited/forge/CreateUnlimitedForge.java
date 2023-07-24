package dev.rdh.createunlimited.forge;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

import dev.rdh.createunlimited.CreateUnlimited;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CreateUnlimited.ID)
public class CreateUnlimitedForge {

    public CreateUnlimitedForge() {
		MinecraftForge.EVENT_BUS.register(Events.ClientModBusEvents.class);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(Events.ClientModBusEvents::onLoadComplete);
		MinecraftForge.EVENT_BUS.addListener(Events::registerCommands);
        CreateUnlimited.init();
    }
}
