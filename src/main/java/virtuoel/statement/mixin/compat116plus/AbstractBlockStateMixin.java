package virtuoel.statement.mixin.compat116plus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import virtuoel.statement.Statement;
import virtuoel.statement.util.StatementBlockStateExtensions;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin implements StatementBlockStateExtensions
{
	@Unique boolean firstOutlineLog = true;
	
	@Inject(at = @At("RETURN"), cancellable = true, method = "getOutlineShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;")
	private void onGetOutlineShape(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> info)
	{
		if (info.getReturnValue() == null)
		{
			if (firstOutlineLog)
			{
				firstOutlineLog = false;
				Statement.LOGGER.warn("BlockState {} returned a null outline shape! This should never happen!", this);
			}
			info.setReturnValue(VoxelShapes.fullCube());
		}
	}
	
	@Unique boolean firstCollisionLog = true;
	
	@Inject(at = @At("RETURN"), cancellable = true, method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;")
	private void onGetCollisionShape(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> info)
	{
		if (info.getReturnValue() == null)
		{
			if (firstCollisionLog)
			{
				firstCollisionLog = false;
				Statement.LOGGER.warn("BlockState {} returned a null collsion shape! This should never happen!", this);
			}
			info.setReturnValue(VoxelShapes.empty());
		}
	}
	
	@Shadow
	abstract Block getBlock();
	
	@Override
	public Block statement_getBlock()
	{
		return getBlock();
	}
}
