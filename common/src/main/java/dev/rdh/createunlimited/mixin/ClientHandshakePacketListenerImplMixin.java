package dev.rdh.createunlimited.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.rdh.createunlimited.CreateUnlimited;
import dev.rdh.createunlimited.config.CUConfigs;

import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;

/**
 * ensures on server join that the server has unlimited installed (things break otherwise)
 */
@Mixin(ClientHandshakePacketListenerImpl.class)
public class ClientHandshakePacketListenerImplMixin {
	@Shadow
	@Final
	@Mutable
	private ServerData serverData;

	@Inject(method = "authenticateServer", at = @At("HEAD"), cancellable = true)
	private void onAuthenticateServer(String serverHash, CallbackInfoReturnable<Component> cir) {
		CreateUnlimited.LOGGER.info("Checking if Create Unlimited is installed on the server...");
		try {
			// this always fails - make it only fail if the server doesn't have the mod installed
			CUConfigs.server.allowAllCopycatBlocks.get();
		} catch (IllegalStateException e) {
			Component c = Component.literal("Create Unlimited is not installed on the server!" +
				"\nPlease either install it or remove it from your client.");
			serverData = null;
			cir.setReturnValue(c);
		}
	}
}
