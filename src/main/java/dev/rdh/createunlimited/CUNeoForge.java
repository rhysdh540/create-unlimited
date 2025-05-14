package dev.rdh.createunlimited;

import dev.rdh.createunlimited.command.CUCommands;
import dev.rdh.createunlimited.config.CUConfig;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

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

		if(FMLLoader.getDist().isClient()) {
			modEventBus.addListener(Client::onClientSetup);
		}
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


	private static class Client {
		static void onClientSetup(FMLClientSetupEvent event) {
			event.enqueueWork(() -> {
				BaseConfigScreen.setDefaultActionFor(CreateUnlimited.ID, base ->
					base.withSpecs(null, null, CUConfig.instance.specification)
						.withButtonLabels("", "", "Settings")
				);
				modContainer.registerExtensionPoint(IConfigScreenFactory.class,
					(container, parent) -> new BaseConfigScreen(parent, CreateUnlimited.ID));
			});
		}
	}
}
