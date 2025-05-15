package dev.rdh.createunlimited.forge;

import dev.rdh.createunlimited.CreateUnlimited;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.registries.Registries;

@Mod(CreateUnlimited.ID)
public final class CreateUnlimitedForge implements CreateUnlimited {

	static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENTS = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, CreateUnlimited.ID);

    public CreateUnlimitedForge() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get()
			.getModEventBus();
		IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

		ARGUMENTS.register(modEventBus);
		forgeEventBus.register(Events.ClientModBusEvents.class);
		forgeEventBus.register(Events.class);
		modEventBus.addListener(Events.ClientModBusEvents::onLoadComplete);
		this.init();
    }
}
