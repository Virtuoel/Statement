package virtuoel.statement.util;

import io.netty.channel.Channel;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import virtuoel.statement.mixin.client.ClientConnectionAccessor;
import virtuoel.statement.mixin.client.ClientPlayNetworkHandlerAccessor;

public class NetworkUtils
{
	public static boolean setAutoRead(final boolean value)
	{
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
		{
			final ClientPlayNetworkHandlerAccessor networkHandler = (ClientPlayNetworkHandlerAccessor) MinecraftClient.getInstance().getNetworkHandler();
			
			if (networkHandler != null)
			{
				final Channel channel = ((ClientConnectionAccessor) networkHandler.statement$getConnection()).statement$getChannel();
				
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
