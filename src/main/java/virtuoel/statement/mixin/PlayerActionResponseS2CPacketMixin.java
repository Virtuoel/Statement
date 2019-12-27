package virtuoel.statement.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.packet.PlayerActionResponseS2CPacket;
import virtuoel.statement.api.StatementApi;

@Mixin(PlayerActionResponseS2CPacket.class)
public class PlayerActionResponseS2CPacketMixin
{
	@Redirect(method = "write(Lnet/minecraft/util/PacketByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getRawIdFromState(Lnet/minecraft/block/BlockState;)I"))
	private int writeGetRawIdFromStateProxy(BlockState state)
	{
		for (final StatementApi api : StatementApi.ENTRYPOINTS)
		{
			final Optional<Integer> syncedId = api.getSyncedId(Block.STATE_IDS, state);
			
			if (syncedId.isPresent())
			{
				return syncedId.get();
			}
		}
		
		return Block.getRawIdFromState(state);
	}
}
