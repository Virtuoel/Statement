package virtuoel.statement.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import virtuoel.statement.api.StateRefresher;

@Mixin(MinecraftDedicatedServer.class)
public class MinecraftDedicatedServerMixin
{
	@Inject(method = "setupServer()Z", at = @At("RETURN"))
	private void onSetupServer(CallbackInfoReturnable<Boolean> info)
	{
		if (info.getReturnValueZ())
		{
			StateRefresher.INSTANCE.reorderBlockStates();
			StateRefresher.INSTANCE.reorderFluidStates();
		}
	}
}
