package virtuoel.statement.util;

import io.netty.channel.Channel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import virtuoel.statement.mixin.client.ClientConnectionAccessor;

public class NetworkUtils
{
	public static boolean setAutoRead(final boolean value)
	{
		if (FMLEnvironment.dist == Dist.CLIENT)
		{
			@SuppressWarnings("unchecked")
			final ClientPlayNetworkHandlerExtensions<ClientConnection> networkHandler = (ClientPlayNetworkHandlerExtensions<ClientConnection>) MinecraftClient.getInstance().getNetworkHandler();
			
			if (networkHandler != null)
			{
				final Channel channel = ((ClientConnectionAccessor) networkHandler.statement_getConnection()).statement$getChannel();
				
				if (channel != null)
				{
					final boolean last = channel.config().isAutoRead();
					
					channel.config().setAutoRead(value);
					
					return last;
				}
			}
		}
		
		return true;
	}
}
