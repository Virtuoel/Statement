package virtuoel.statement.mixin.client.compat1202plus;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import virtuoel.statement.util.ClientPlayNetworkHandlerExtensions;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler implements ClientPlayNetworkHandlerExtensions<ClientConnection>
{
	protected ClientPlayNetworkHandlerMixin()
	{
		super(null, null, null);
	}
	
	@Override
	public ClientConnection statement_getConnection()
	{
		return connection;
	}
}
