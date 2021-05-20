package virtuoel.statement.mixin.compat117plus;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

@Mixin(RedstoneWireBlock.class)
public class RedstoneWireBlockMixin
{
	@Shadow @Final @Mutable
	private static Map<BlockState, VoxelShape> SHAPES;
	
	@Inject(at = @At("RETURN"), method = "getOutlineShape", cancellable = true)
	private void onGetOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> info)
	{
		if (info.getReturnValue() == null)
		{
			final VoxelShape shape = SHAPES.get(
				state.getBlock().getDefaultState()
					.with(Properties.POWER, 0)
					.with(Properties.NORTH_WIRE_CONNECTION, state.get(Properties.NORTH_WIRE_CONNECTION))
					.with(Properties.SOUTH_WIRE_CONNECTION, state.get(Properties.SOUTH_WIRE_CONNECTION))
					.with(Properties.EAST_WIRE_CONNECTION, state.get(Properties.EAST_WIRE_CONNECTION))
					.with(Properties.WEST_WIRE_CONNECTION, state.get(Properties.WEST_WIRE_CONNECTION))
			);
			
			SHAPES.put(state, shape);
			
			info.setReturnValue(shape);
		}
	}
}
