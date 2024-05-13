package dev.rdh.createunlimited.forge;


import dev.rdh.createunlimited.CreateUnlimited;

import dev.rdh.createunlimited.command.CUCommands;
import dev.rdh.createunlimited.config.CUConfigs;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import net.minecraft.commands.Commands.CommandSelection;

public abstract class Events {
	@Mod.EventBusSubscriber(modid = CreateUnlimited.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static abstract class ClientModBusEvents {
		@SubscribeEvent
		static void onLoadComplete(FMLLoadCompleteEvent event) {
			ModContainer container = ModList.get()
				.getModContainerById(CreateUnlimited.ID)
				.orElseThrow(() -> new IllegalStateException("Create Unlimited mod container missing on LoadComplete"));
			container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
				() -> new ConfigScreenHandler.ConfigScreenFactory(CUConfigs::createConfigScreen));
		}
	}

	@SubscribeEvent
	static void registerCommands(RegisterCommandsEvent event) {
		CUCommands.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
	}

	@SubscribeEvent
	static void onConfigLoad(ModConfigEvent.Loading event) {
		CUConfigs.onLoad(event.getConfig());
	}

	@SubscribeEvent
	static void onConfigReload(ModConfigEvent.Reloading event) {
		CUConfigs.onReload(event.getConfig());
	}
}