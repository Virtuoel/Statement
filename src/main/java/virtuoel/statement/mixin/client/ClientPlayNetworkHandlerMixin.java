package virtuoel.statement.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import virtuoel.statement.api.StateRefresher;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin
{
	@Inject(at = @At("RETURN"), method = "onGameJoin")
	private void onOnGameJoin(GameJoinS2CPacket packet, CallbackInfo info)
	{
		if (!MinecraftClient.getInstance().isInSingleplayer())
		{
			StateRefresher.INSTANCE.reorderBlockStates();
			StateRefresher.INSTANCE.reorderFluidStates();
		}
	}
}
