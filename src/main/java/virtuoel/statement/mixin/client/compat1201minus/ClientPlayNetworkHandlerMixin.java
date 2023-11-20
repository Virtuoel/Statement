package virtuoel.statement.mixin.client.compat1201minus;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import virtuoel.statement.util.ClientPlayNetworkHandlerExtensions;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin implements ClientPlayNetworkHandlerExtensions<ClientConnection>
{
	@Shadow(remap = false) @Final @Mutable ClientConnection field_3689; // UNMAPPED_FIELD
	
	@Override
	public ClientConnection statement_getConnection()
	{
		return field_3689;
	}
}
