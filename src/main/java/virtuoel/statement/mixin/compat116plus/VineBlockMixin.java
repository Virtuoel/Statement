package virtuoel.statement.mixin.compat116plus;

import java.util.LinkedHashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

@Mixin(VineBlock.class)
public class VineBlockMixin
{
	@Shadow @Final @Mutable private Map<BlockState, VoxelShape> shapesByState;
	
	@Inject(at = @At("RETURN"), method = "<init>")
	private void onConstruct(Block.Settings settings, CallbackInfo info)
	{
		if (shapesByState instanceof ImmutableMap)
		{
			shapesByState = new LinkedHashMap<>(shapesByState);
		}
	}
	
	@Inject(at = @At("RETURN"), method = "getOutlineShape", cancellable = true)
	private void onGetOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> info)
	{
		if (info.getReturnValue() == null)
		{
			final VoxelShape shape = shapesByState.get(
				state.getBlock().getDefaultState()
					.with(Properties.UP, state.get(Properties.UP))
					.with(Properties.NORTH, state.get(Properties.NORTH))
					.with(Properties.SOUTH, state.get(Properties.SOUTH))
					.with(Properties.EAST, state.get(Properties.EAST))
					.with(Properties.WEST, state.get(Properties.WEST))
			);
			
			shapesByState.put(state, shape);
			
			info.setReturnValue(shape);
		}
	}
}
