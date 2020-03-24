package virtuoel.statement.mixin.compat116plus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import virtuoel.statement.util.StatementBlockStateExtensions;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin implements StatementBlockStateExtensions
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
