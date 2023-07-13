package dev.rdh.createunlimited.forge;


import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import com.simibubi.create.foundation.config.ui.BaseConfigScreen;

import dev.rdh.createunlimited.CreateUnlimited;

import dev.rdh.createunlimited.config.CUConfig;

import net.minecraft.commands.CommandSourceStack;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;

@Mod(CreateUnlimited.MOD_ID)
@Mod.EventBusSubscriber(modid = CreateUnlimited.MOD_ID)
public class CreateUnlimitedForge {

    public CreateUnlimitedForge() {
        CreateUnlimited.init();
        MinecraftForge.EVENT_BUS.register(CreateUnlimitedForge.class);
    }

    @SubscribeEvent
    static void registerCommands(RegisterCommandsEvent event) {
        for(LiteralArgumentBuilder<CommandSourceStack> command : CUPlatformFunctionsImpl.commands)
            event.getDispatcher().register(command);
    }

	@SubscribeEvent
	static void onLoadComplete(FMLLoadCompleteEvent event) {
		ModContainer createContainer = ModList.get()
			.getModContainerById(CreateUnlimited.MOD_ID)
			.orElseThrow(() -> new IllegalStateException("Create Unlimited mod container missing on LoadComplete"));
		createContainer.registerExtensionPoint(ConfigScreenFactory.class,
			() -> new ConfigScreenFactory(CreateUnlimited::createConfigScreen));
	}
}
