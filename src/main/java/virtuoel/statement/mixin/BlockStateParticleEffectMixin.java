package virtuoel.statement.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.util.PacketByteBuf;
import virtuoel.statement.api.StatementApi;

@Mixin(BlockStateParticleEffect.class)
public class BlockStateParticleEffectMixin
{
	@Shadow @Final private BlockState blockState;
	
	@Inject(at = @At("HEAD"), method = "write", cancellable = true)
	private void onWrite(PacketByteBuf buf, CallbackInfo info)
	{
		for (final StatementApi api : StatementApi.ENTRYPOINTS)
		{
			final Optional<Integer> syncedId = api.getSyncedId(Block.STATE_IDS, blockState);
			syncedId.ifPresent(buf::writeVarInt);
			
			if (syncedId.isPresent())
			{
				info.cancel();
				break;
			}
		}
	}
}
