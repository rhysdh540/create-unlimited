package dev.rdh.createunlimited.forge;


import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.rdh.createunlimited.CreateUnlimited;

import dev.rdh.createunlimited.config.CUConfig;

import net.minecraft.commands.CommandSourceStack;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;

@Mod(CreateUnlimited.ID)
@Mod.EventBusSubscriber(modid = CreateUnlimited.ID)
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
		ModContainer container = ModList.get()
			.getModContainerById(CreateUnlimited.ID)
			.orElseThrow(() -> new IllegalStateException("Create Unlimited mod container missing on LoadComplete"));
		container.registerExtensionPoint(ConfigScreenFactory.class,
			() -> new ConfigScreenFactory(CUConfig::createConfigScreen));
	}
}
