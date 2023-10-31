package virtuoel.statement.util;

import io.netty.channel.Channel;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import virtuoel.statement.mixin.client.ClientConnectionAccessor;

public class NetworkUtils
{
	public static boolean setAutoRead(final boolean value)
	{
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
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
