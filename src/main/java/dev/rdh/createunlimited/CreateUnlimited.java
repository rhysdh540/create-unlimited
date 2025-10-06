package dev.rdh.createunlimited;

import dev.rdh.createunlimited.command.CUCommands;
import dev.rdh.createunlimited.config.CUConfig;

import dev.rdh.createunlimited.command.EnumArgument;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.api.contraption.BlockMovementChecks.CheckResult;

#if forge
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
#elif neoforge
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLLoader;
#endif

public #if forgelike sealed #else final #endif
class CreateUnlimited #if fabric implements net.fabricmc.api.ModInitializer #endif {
	public static final String ID = "createunlimited";
	public static final String NAME = "Create Unlimited";
	public static final String VERSION = Util.getVersion(ID).split("-build")[0];
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	private static CreateUnlimited instance;

	public static CreateUnlimited getInstance() {
		if (instance == null) {
			throw new IllegalStateException("CreateUnlimited instance is not initialized yet!");
		}
		return instance;
	}

	#if forge
	public final FMLJavaModLoadingContext forgeContext;
	#endif

	#if forgelike
	public final ModContainer modContainer;
	#endif

	#if fabric @Override public void onInitialize()
	#else public CreateUnlimited
	#if forge (FMLJavaModLoadingContext context)
	#elif neoforge (IEventBus modBus, ModContainer container)
	#endif #endif {
		instance = this;
		#if forge
		this.forgeContext = context;
		var modBus = context.getModEventBus();
		var gameBus = net.minecraftforge.common.MinecraftForge.EVENT_BUS;
		this.modContainer = context.getContainer();
		#elif neoforge
		var gameBus = net.neoforged.neoforge.common.NeoForge.EVENT_BUS;
		this.modContainer = container;
		#endif
		#if forgelike
		Util.ARGUMENTS.register(modBus);

		gameBus.addListener(this::registerCommands);
		modBus.addListener(this::onConfigLoad);
		modBus.addListener(this::onConfigReload);
		if (FMLLoader.getDist().isClient()) {
			modBus.addListener(ForgeClient::onClientSetup);
		}
		#endif

		#if fabric
		net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register(CUCommands::register);
		fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents.loading(ID).register(CUConfig::onLoad);
		fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents.reloading(ID).register(CUConfig::onReload);
		#endif

		LOGGER.info("{} v{} initializing on platform: {}!", NAME, VERSION, Util.platformName());

		EnumArgument.init();
		CUConfig.register();

		BlockMovementChecks.registerMovementAllowedCheck((s, l, p) -> {
			if (CUConfig.getOrFalse(CUConfig.instance.allowContraptionMoveAll)) {
				return CheckResult.SUCCESS;
			} else {
				return CheckResult.PASS;
			}
		});
    }

	#if forgelike
	void registerCommands(RegisterCommandsEvent event) {
		CUCommands.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
	}

	void onConfigLoad(ModConfigEvent.Loading event) {
		CUConfig.onLoad(event.getConfig());
	}

	void onConfigReload(ModConfigEvent.Reloading event) {
		CUConfig.onReload(event.getConfig());
	}

	#endif

	public static ResourceLocation asResource(String path) {
		#if fabric && MC < 21.0
		return new ResourceLocation(ID, path);
		#else
		return ResourceLocation.fromNamespaceAndPath(ID, path);
		#endif
	}

	// forge doesn't recognize transformer transformed classes, so we just gotta make new ones
	// thanks forge
	#if forge
	@net.minecraftforge.fml.common.Mod(ID)
	public static final class ForgeInit extends CreateUnlimited {
		public ForgeInit(FMLJavaModLoadingContext context) {
			super(context);
		}
	}
	#elif neoforge
	@net.neoforged.fml.common.Mod(ID)
	public static final class NeoForgeInit extends CreateUnlimited {
		public NeoForgeInit(IEventBus modBus, ModContainer container) {
			super(modBus, container);
		}
	}
	#endif

	#if forgelike
	public static final class ForgeClient {
		static void onClientSetup(FMLClientSetupEvent event) {
			event.enqueueWork(() -> {
			#if neoforge
				// TODO: make this not crash the game on dedicated server
				instance.modContainer.registerExtensionPoint(net.neoforged.neoforge.client.gui.IConfigScreenFactory.class,
					(container, parent) -> CUConfig.ScreenManager.createConfigScreen(parent));
			#elif forge
			instance.modContainer.registerExtensionPoint(net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory.class,
				() -> new net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory(CUConfig.ScreenManager::createConfigScreen));
			#endif
			});
		}
	}
	#endif
}
