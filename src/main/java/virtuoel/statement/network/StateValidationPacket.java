package virtuoel.statement.network;

import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.NetworkEvent;

public class StateValidationPacket
{
	private UUID uuid;
	private int rate;
	private int[] ids;
	
	public StateValidationPacket(PlayerEntity executor, int rate, int initialId)
	{
		this.uuid = executor.getUuid();
		this.rate = rate;
		
		ids = new int[rate];
		
		for (int i = 0; i < rate; i++)
		{
			ids[i] = initialId + i;
		}
	}
	
	protected StateValidationPacket(PacketByteBuf buf)
	{
		this.uuid = buf.readUuid();
		this.rate = buf.readVarInt();
		
		ids = new int[rate];
		
		for (int i = 0; i < rate; i++)
		{
			ids[i] = buf.readVarInt();
		}
	}
	
	public static void handle(StateValidationPacket msg, NetworkEvent.Context ctx)
	{
		// NYI
		
		ctx.enqueueWork(() ->
		{
			if (FMLEnvironment.dist == Dist.CLIENT)
			{
				// client
				// NYI
			}
			else
			{
				// server
				// NYI
			}
		});
		
		ctx.setPacketHandled(true);
		
	}
	
	public void encode(PacketByteBuf buf)
	{
		buf.writeUuid(uuid).writeVarInt(rate);
		
		for (int i = 0; i < rate; i++)
		{
			buf.writeVarInt(ids[i]);
		}
	}
}
