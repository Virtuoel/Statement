package virtuoel.statement.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public interface StatementBlockStateExtensions extends StatementStateExtensions<BlockState>
{
	Block statement_getBlock();
}
