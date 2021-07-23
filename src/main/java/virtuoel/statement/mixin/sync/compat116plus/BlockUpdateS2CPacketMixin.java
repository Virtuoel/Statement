package virtuoel.statement.mixin.sync.compat116plus;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import virtuoel.statement.Statement;

@Mixin(BlockUpdateS2CPacket.class)
public class BlockUpdateS2CPacketMixin
{
	@Shadow @Final @Mutable BlockState state;
	
	@Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V")
	private void onConstruct(BlockPos pos, BlockState state, CallbackInfo info)
	{
		Statement.getSyncedBlockStateId(state).ifPresent(id ->
		{
			this.state = Block.getStateFromRawId(id);
		});
	}
}
