package virtuoel.statement.mixin;

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
import net.minecraft.block.WallBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

@Mixin(WallBlock.class)
public class WallBlockMixin
{
	@Shadow @Final @Mutable private Map<BlockState, VoxelShape> shapeMap;
	@Shadow @Final @Mutable private Map<BlockState, VoxelShape> collisionShapeMap;
	
	@Inject(at = @At("RETURN"), method = "<init>")
	private void onConstruct(Block.Settings settings, CallbackInfo info)
	{
		if (shapeMap instanceof ImmutableMap)
		{
			shapeMap = new LinkedHashMap<>(shapeMap);
		}
		
		if (collisionShapeMap instanceof ImmutableMap)
		{
			collisionShapeMap = new LinkedHashMap<>(collisionShapeMap);
		}
	}
	
	@Inject(at = @At("RETURN"), method = "getOutlineShape", cancellable = true)
	private void onGetOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context, CallbackInfoReturnable<VoxelShape> info)
	{
		if (info.getReturnValue() == null)
		{
			final VoxelShape shape = shapeMap.get(
				state.getBlock().getDefaultState()
					.with(Properties.UP, state.get(Properties.UP))
					.with(Properties.NORTH_WALL_SHAPE, state.get(Properties.NORTH_WALL_SHAPE))
					.with(Properties.SOUTH_WALL_SHAPE, state.get(Properties.SOUTH_WALL_SHAPE))
					.with(Properties.EAST_WALL_SHAPE, state.get(Properties.EAST_WALL_SHAPE))
					.with(Properties.WEST_WALL_SHAPE, state.get(Properties.WEST_WALL_SHAPE))
			);
			
			shapeMap.put(state, shape);
			
			info.setReturnValue(shape);
		}
	}
	
	@Inject(at = @At("RETURN"), method = "getCollisionShape", cancellable = true)
	private void onGetCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext context, CallbackInfoReturnable<VoxelShape> info)
	{
		if (info.getReturnValue() == null)
		{
			final VoxelShape shape = collisionShapeMap.get(
				state.getBlock().getDefaultState()
					.with(Properties.UP, state.get(Properties.UP))
					.with(Properties.NORTH_WALL_SHAPE, state.get(Properties.NORTH_WALL_SHAPE))
					.with(Properties.SOUTH_WALL_SHAPE, state.get(Properties.SOUTH_WALL_SHAPE))
					.with(Properties.EAST_WALL_SHAPE, state.get(Properties.EAST_WALL_SHAPE))
					.with(Properties.WEST_WALL_SHAPE, state.get(Properties.WEST_WALL_SHAPE))
			);
			
			collisionShapeMap.put(state, shape);
			
			info.setReturnValue(shape);
		}
	}
}
