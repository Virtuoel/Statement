package virtuoel.statement.mixin.compat116;

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
	private Map<BlockState, VoxelShape> field_24416;
	
	@Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V")
	private void onConstruct(Block.Settings settings, CallbackInfo info)
	{
		if (field_24416 instanceof ImmutableMap)
		{
			field_24416 = new LinkedHashMap<>(field_24416);
		}
	}
	
	@Inject(at = @At("RETURN"), method = "getOutlineShape", cancellable = true)
	private void onGetOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> info)
	{
		if (info.getReturnValue() == null)
		{
			final VoxelShape shape = field_24416.get(
				state.getBlock().getDefaultState()
					.with(Properties.POWER, state.get(Properties.POWER))
					.with(Properties.NORTH_WIRE_CONNECTION, state.get(Properties.NORTH_WIRE_CONNECTION))
					.with(Properties.SOUTH_WIRE_CONNECTION, state.get(Properties.SOUTH_WIRE_CONNECTION))
					.with(Properties.EAST_WIRE_CONNECTION, state.get(Properties.EAST_WIRE_CONNECTION))
					.with(Properties.WEST_WIRE_CONNECTION, state.get(Properties.WEST_WIRE_CONNECTION))
			);
			
			field_24416.put(state, shape);
			
			info.setReturnValue(shape);
		}
	}
}
