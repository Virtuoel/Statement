package virtuoel.statement.util;

import net.minecraft.block.Block;

public interface StatementBlockStateExtensions extends StatementStateExtensions
{
	void statement_initShapeCache();
	
	Block statement_getBlock();
}
