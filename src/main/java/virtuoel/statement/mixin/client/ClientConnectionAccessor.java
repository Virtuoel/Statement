package virtuoel.statement.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;

@Mixin(ClientConnection.class)
public interface ClientConnectionAccessor
{
	@Accessor("channel")
	Channel statement$getChannel();
}
