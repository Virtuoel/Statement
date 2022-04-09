package virtuoel.statement.mixin.sync.compat118;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.util.math.BlockPos;
import virtuoel.statement.Statement;

@Mixin(PlayerActionResponseS2CPacket.class)
public class PlayerActionResponseS2CPacketMixin
{
	@Shadow(remap = false) @Final @Mutable private BlockState comp_85;
	
	@Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_2846$class_2847;Z)V", remap = false)
	private void onConstruct(BlockPos pos, BlockState state, PlayerActionC2SPacket.Action action, boolean approved, String reason, CallbackInfo info)
	{
		Statement.getSyncedBlockStateId(state).ifPresent(id ->
		{
			this.comp_85 = Block.getStateFromRawId(id);
		});
	}
}
