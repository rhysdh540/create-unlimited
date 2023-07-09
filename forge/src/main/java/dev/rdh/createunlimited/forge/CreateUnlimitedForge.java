package dev.rdh.createunlimited.forge;


import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.rdh.createunlimited.CreateUnlimited;

import dev.rdh.createunlimited.config.CUConfig;

import net.minecraft.commands.CommandSourceStack;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(CreateUnlimited.MOD_ID)
@Mod.EventBusSubscriber(modid = CreateUnlimited.MOD_ID)
public class CreateUnlimitedForge {

    public CreateUnlimitedForge() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CUConfig.SPEC, "createunlimited.toml");
        CreateUnlimited.init();
        MinecraftForge.EVENT_BUS.register(CreateUnlimitedForge.class);
    }

    @SubscribeEvent
    static void registerCommands(RegisterCommandsEvent event) {
        for(LiteralArgumentBuilder<CommandSourceStack> command : CUPlatformFunctionsImpl.commands)
            event.getDispatcher().register(command);
    }
}
