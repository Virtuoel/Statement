package virtuoel.statement.mixin.sync;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.util.PacketByteBuf;
import virtuoel.statement.Statement;

@Mixin(BlockStateParticleEffect.class)
public class BlockStateParticleEffectMixin
{
	@Shadow @Final private BlockState blockState;
	
	@Inject(at = @At("HEAD"), method = "write", cancellable = true)
	private void onWrite(PacketByteBuf buf, CallbackInfo info)
	{
		Statement.getSyncedBlockStateId(this.blockState).ifPresent(id ->
		{
			buf.writeVarInt(id);
			info.cancel();
		});
	}
}
