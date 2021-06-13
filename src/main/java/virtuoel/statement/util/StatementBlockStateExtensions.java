package virtuoel.statement.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public interface StatementBlockStateExtensions extends StatementStateExtensions<BlockState>
{
	void statement_initShapeCache();
	
	Block statement_getBlock();
}
