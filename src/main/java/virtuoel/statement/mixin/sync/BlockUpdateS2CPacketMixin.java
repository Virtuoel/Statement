package virtuoel.statement.mixin.sync;

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
import net.minecraft.world.BlockView;
import virtuoel.statement.Statement;

@Mixin(BlockUpdateS2CPacket.class)
public class BlockUpdateS2CPacketMixin
{
	@Shadow @Final @Mutable BlockState state;
	
	@Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)V")
	private void onConstruct(BlockView world, BlockPos pos, CallbackInfo info)
	{
		state = Block.getStateFromRawId(Statement.getSyncedBlockStateId(state).orElseGet(() -> Block.getRawIdFromState(state)));
	}
}
