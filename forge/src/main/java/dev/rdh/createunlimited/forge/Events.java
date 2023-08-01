package dev.rdh.createunlimited.forge;

import dev.rdh.createunlimited.CreateUnlimited;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

public abstract class Events {
	@Mod.EventBusSubscriber(modid = CreateUnlimited.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static abstract class ClientModBusEvents {
	}
}