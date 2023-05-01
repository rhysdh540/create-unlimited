package dev.rdh.createunlimited.forge;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import dev.rdh.createunlimited.CreateUnlimited;
import net.minecraftforge.fml.common.Mod;

@Mod(CreateUnlimited.MOD_ID)
@Mod.EventBusSubscriber(modid = CreateUnlimited.MOD_ID)
public class CreateUnlimitedForge {
    public CreateUnlimitedForge() {
//        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
//                () -> new ConfigScreenHandler.ConfigScreenFactory((minecraftClient, screen) -> /*something*/));
                // double lambda o.O
        CreateUnlimited.init();
        MinecraftForge.EVENT_BUS.register(CreateUnlimitedForge.class);
    }
    @SubscribeEvent
    static void registerCommands(RegisterCommandsEvent event) {
        for(LiteralArgumentBuilder<CommandSourceStack> command : CUPlatformFunctionsImpl.commands)
            event.getDispatcher().register(command);
    }
}
