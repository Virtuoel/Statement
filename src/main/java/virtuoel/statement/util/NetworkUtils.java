package virtuoel.statement.util;

import io.netty.channel.Channel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import virtuoel.statement.mixin.client.ClientConnectionAccessor;

public class NetworkUtils
{
	public static boolean setAutoRead(final boolean value)
	{
		if (FMLEnvironment.dist == Dist.CLIENT)
		{
			final ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
			
			if (networkHandler != null)
			{
				final Channel channel = ((ClientConnectionAccessor) networkHandler.getConnection()).getChannel();
				
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
