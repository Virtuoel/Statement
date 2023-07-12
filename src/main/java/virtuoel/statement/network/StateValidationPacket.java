package virtuoel.statement.network;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

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
	
	public static void handle(StateValidationPacket msg, Supplier<NetworkEvent.Context> ctx)
	{
		// NYI
		
		ctx.get().enqueueWork(() ->
		{
			DistExecutor.unsafeRunForDist(() -> () ->
			{
				// NYI
				
				return "client";
			},
			() -> () ->
			{
				// NYI
				
				return "server";
			});
		});
		
		ctx.get().setPacketHandled(true);
		
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
