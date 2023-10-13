package dev.rdh.createunlimited.forge;


import dev.rdh.createunlimited.CreateUnlimited;

import dev.rdh.createunlimited.config.CUConfigs;

import java.util.HashSet;
import java.util.Set;

import manifold.rt.api.NoBootstrap;

import net.minecraft.commands.CommandSourceStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import org.jetbrains.annotations.ApiStatus;

@NoBootstrap
public abstract class Events {

	@ApiStatus.Internal
	public static Set<LiteralArgumentBuilder<CommandSourceStack>> commands = new HashSet<>();

	@NoBootstrap
	@EventBusSubscriber(modid = CreateUnlimited.ID, bus = Bus.MOD, value = Dist.CLIENT)
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
		for(var command : commands)
			event.getDispatcher().register(command);
	}
}