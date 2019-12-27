package virtuoel.statement.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.client.network.packet.WorldEventS2CPacket;
import net.minecraft.util.math.BlockPos;
import virtuoel.statement.api.StatementApi;

@Mixin(WorldEventS2CPacket.class)
public class WorldEventS2CPacketMixin
{
	@Shadow int data;
	
	@Inject(at = @At("RETURN"), method = "<init>")
	private void onConstruct(int eventId, BlockPos pos, int data, boolean global, CallbackInfo info)
	{
		if (eventId == 2001)
		{
			for (final StatementApi api : StatementApi.ENTRYPOINTS)
			{
				final Optional<Integer> syncedId = api.getSyncedId(Block.STATE_IDS, data);
				
				if(syncedId.isPresent())
				{
					this.data = syncedId.get();
					break;
				}
			}
		}
	}
}
