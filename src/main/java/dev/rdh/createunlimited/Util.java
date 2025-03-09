package dev.rdh.createunlimited;

import com.mojang.brigadier.arguments.ArgumentType;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.entity.ai.attributes.Attribute;

import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public interface Util {
	String getVersion(String modid);

	boolean isDevEnv();

	String platformName();

	void registerConfig(ModConfig.Type type, IConfigSpec<?> spec);

	<A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>>
	void registerArgument(Class<A> clazz, I info, ResourceLocation id);

	Attribute getReachAttribute();

	Util INSTANCE = getInstance();

	@Deprecated(forRemoval = true)
	@SuppressWarnings("DeprecatedIsStillUsed")
	static Util getInstance() {
		try {
			String cn = System.getProperty("createunlimited.util.classname");
			if (cn != null) {
				return (Util) Class.forName(cn).getDeclaredConstructor().newInstance();
			} else {
				throw new IllegalStateException("No Util implementation found");
			}
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
}
