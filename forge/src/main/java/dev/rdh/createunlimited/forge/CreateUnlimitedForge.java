package dev.rdh.createunlimited.forge;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.rdh.createunlimited.config.CUConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import dev.rdh.createunlimited.CreateUnlimited;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

/**
 * The main class for the Forge implementation of Create Unlimited. It is called by Forge when the game starts.
 * @see dev.rdh.createunlimited.CreateUnlimited CreateUnlimited
 */
@Mod(CreateUnlimited.MOD_ID)
@Mod.EventBusSubscriber(modid = CreateUnlimited.MOD_ID)
public class CreateUnlimitedForge {
    /**
     * This constructor is what is called by Forge when the game starts. It initializes the common code, and registers the command registration event listener.
     */
    public CreateUnlimitedForge() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CUConfig.SPEC, "createunlimited.toml");
        CreateUnlimited.init();
        MinecraftForge.EVENT_BUS.register(CreateUnlimitedForge.class);
    }

    /**
     * This method is called by Forge when the command registration event is fired. It registers all the commands that have been added to the list.
     * @param event the command registration event
     * @see net.minecraftforge.event.RegisterCommandsEvent RegisterCommandsEvent
     * @see dev.rdh.createunlimited.CUPlatformFunctions#registerCommand(LiteralArgumentBuilder) CUPlatformFunctions.registerCommand(LiteralArgumentBuilder)
     * @see dev.rdh.createunlimited.forge.CUPlatformFunctionsImpl#commands
     */
    @SubscribeEvent
    static void registerCommands(RegisterCommandsEvent event) {
        for(LiteralArgumentBuilder<CommandSourceStack> command : CUPlatformFunctionsImpl.commands)
            event.getDispatcher().register(command);
    }
}
