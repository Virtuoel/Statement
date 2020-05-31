package virtuoel.statement.mixin.compat115minus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import virtuoel.statement.util.StatementBlockStateExtensions;

@Mixin(BlockState.class)
public abstract class BlockStateMixin implements StatementBlockStateExtensions
{
	@Shadow
	abstract void initShapeCache();
	@Shadow
	abstract Block getBlock();
	
	@Override
	public void statement_initShapeCache()
	{
		initShapeCache();
	}
	
	@Override
	public Block statement_getBlock()
	{
		return getBlock();
	}
}
