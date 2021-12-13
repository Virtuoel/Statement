package virtuoel.statement.mixin.compat117plus;

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

import net.minecraft.block.BigDripleafBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

@Mixin(BigDripleafBlock.class)
public class BigDripleafBlockMixin
{
	@Shadow @Final @Mutable private Map<BlockState, VoxelShape> shapes;
	
	@Inject(at = @At("RETURN"), method = "<init>")
	private void onConstruct(Block.Settings settings, CallbackInfo info)
	{
		if (shapes instanceof ImmutableMap)
		{
			shapes = new LinkedHashMap<>(shapes);
		}
	}
	
	@Inject(at = @At("RETURN"), method = "getOutlineShape", cancellable = true)
	private void onGetOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> info)
	{
		if (info.getReturnValue() == null)
		{
			final VoxelShape shape = shapes.get(
				state.getBlock().getDefaultState()
					.with(Properties.TILT, state.get(Properties.TILT))
					.with(Properties.HORIZONTAL_FACING, state.get(Properties.HORIZONTAL_FACING))
			);
			
			shapes.put(state, shape);
			
			info.setReturnValue(shape);
		}
	}
}
