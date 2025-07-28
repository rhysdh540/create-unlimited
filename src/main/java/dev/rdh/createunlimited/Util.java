package dev.rdh.createunlimited;

import com.mojang.brigadier.arguments.ArgumentType;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.ResourceLocation;

#if MC < 21
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
#else
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
#endif

#if forge
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.ModList;
#elif neoforge
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.fml.ModList;
#endif

public final class Util {

	public static String getVersion(String modid) {
		return
		#if forgelike
			ModList.get().getModContainerById(modid)
				.map(c -> c.getModInfo().getVersion().toString())
		#elif fabric
			net.fabricmc.loader.api.FabricLoader.getInstance()
				.getModContainer(modid)
				.map(c -> c.getMetadata().getVersion().getFriendlyString())
		#else
			#error "Unsupported platform"
		#endif
				.orElseThrow(() -> new IllegalStateException("Mod " + modid + " not found"));
	}

	public static boolean isDevEnv() {
		return
		#if fabric
			net.fabricmc.loader.api.FabricLoader.getInstance().isDevelopmentEnvironment();
		#elif neoforge
			!net.neoforged.fml.loading.FMLLoader.isProduction();
		#elif forge
			!net.minecraftforge.fml.loading.FMLLoader.isProduction();
		#else
			net.minecraft.SharedConstants.IS_RUNNING_IN_IDE;
		#endif
	}

	public static String platformName() {
		return
		#if fabric
			net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("quilt_loader") ? "Quilt" : "Fabric";
		#elif neoforge
			"NeoForge";
		#elif forge
			"Forge";
		#else
			#error "Unsupported platform"
		#endif
	}

	public static void registerConfig(ModConfig.Type type, IConfigSpec spec) {
		#if forge
		CreateUnlimited.getInstance().forgeContext.registerConfig(type, spec);
		#elif neoforge
		net.neoforged.fml.ModLoadingContext.get().getActiveContainer().registerConfig(type, spec);
		#elif fabric
		fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry.INSTANCE
			.register(CreateUnlimited.ID, type, spec);
		#endif
	}

	#if forgelike
	static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENTS =
		DeferredRegister.create(net.minecraft.core.registries.Registries.COMMAND_ARGUMENT_TYPE, CreateUnlimited.ID);
	#endif

	public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>>
	void registerArgument(Class<A> clazz, I info, ResourceLocation id) {
		#if forgelike
		ARGUMENTS.register(id.getPath(),
			() -> net.minecraft.commands.synchronization.ArgumentTypeInfos.registerByClass(clazz, info));
		#elif fabric
		net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry.registerArgumentType(id, clazz, info);
		#endif
	}
}
