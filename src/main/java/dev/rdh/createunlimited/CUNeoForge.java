package dev.rdh.createunlimited;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import dev.rdh.createunlimited.command.CUCommands;
import dev.rdh.createunlimited.config.CUConfig;

@Mod(CreateUnlimited.ID)
public class CUNeoForge implements CreateUnlimited {
	public static ModContainer modContainer;

	public CUNeoForge(IEventBus modEventBus, ModContainer modContainer) {
		CUNeoForge.modContainer = modContainer;
		CreateUnlimited.init();

		NeoForge.EVENT_BUS.addListener(this::registerCommands);
		Util.ARGUMENTS.register(modEventBus);
		modEventBus.addListener(this::onConfigLoad);
		modEventBus.addListener(this::onConfigReload);
		modEventBus.addListener(this::onLoadComplete);
	}

	void registerCommands(RegisterCommandsEvent event) {
		CUCommands.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
	}

	void onConfigLoad(ModConfigEvent.Loading event) {
		CUConfig.onLoad(event.getConfig());
	}

	void onConfigReload(ModConfigEvent.Reloading event) {
		CUConfig.onReload(event.getConfig());
	}

	void onLoadComplete(FMLLoadCompleteEvent event) {
		modContainer.registerExtensionPoint(IConfigScreenFactory.class,
			(container, parent) -> CUConfig.createConfigScreen(parent));
	}
}
