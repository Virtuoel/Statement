package virtuoel.statement.mixin.compat117plus;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

@Mixin(/*AbstractLichen*/Block.class) // TODO FIXME
public class AbstractLichenBlockMixin
{
	/*
	@Shadow
	private static VoxelShape getShapeForState(BlockState state)
	{
		throw new NoSuchMethodError();
	}
	
	@Shadow @Final @Mutable private ImmutableMap<BlockState, VoxelShape> SHAPES;
	
	@Inject(at = @At("RETURN"), method = "getOutlineShape", cancellable = true)
	private void onGetOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> info)
	{
		if (info.getReturnValue() == null)
		{
			SHAPES = ((Block) (Object) this).getStateManager().getStates().stream().collect(ImmutableMap.toImmutableMap(Function.identity(), AbstractLichenBlockMixin::getShapeForState));
			
			info.setReturnValue(SHAPES.get(state));
		}
	}
	*/
}
