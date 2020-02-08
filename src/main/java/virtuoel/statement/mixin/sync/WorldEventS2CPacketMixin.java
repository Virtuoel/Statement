package virtuoel.statement.mixin.sync;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.util.math.BlockPos;
import virtuoel.statement.Statement;

@Mixin(WorldEventS2CPacket.class)
public class WorldEventS2CPacketMixin
{
	@Shadow int data;
	
	@Inject(at = @At("RETURN"), method = "<init>(ILnet/minecraft/util/math/BlockPos;IZ)V")
	private void onConstruct(int eventId, BlockPos pos, int data, boolean global, CallbackInfo info)
	{
		if (eventId == 2001)
		{
			Statement.getSyncedBlockStateId(Block.STATE_IDS.get(data)).ifPresent(id ->
			{
				this.data = id;
			});
		}
	}
}
