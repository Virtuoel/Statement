package virtuoel.statement.mixin.compat115minus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import virtuoel.statement.util.StatementBlockStateExtensions;

@Mixin(BlockState.class)
public abstract class BlockStateMixin implements StatementBlockStateExtensions
{
	@Shadow(remap = false)
	abstract void method_11590();
	@Shadow(remap = false)
	abstract Block method_11614();
	
	@Override
	public void statement_initShapeCache()
	{
		method_11590();
	}
	
	@Override
	public Block statement_getBlock()
	{
		return method_11614();
	}
}
