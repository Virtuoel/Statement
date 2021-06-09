package virtuoel.statement.mixin.compat115minus;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import virtuoel.statement.util.StatementPropertyExtensions;

@Mixin(targets = "net.minecraft.class_2769", remap = false)
public interface PropertyMixin<T extends Comparable<T>> extends StatementPropertyExtensions<T>
{
	@Shadow(remap = false)
	String method_11899();
	@Shadow(remap = false)
	Collection<T> method_11898();
	
	@Override
	default String statement_getName()
	{
		return method_11899();
	}
	
	@Override
	default Collection<T> statement_getValues()
	{
		return method_11898();
	}
}
