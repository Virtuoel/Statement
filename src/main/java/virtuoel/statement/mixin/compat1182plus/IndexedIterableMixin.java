package virtuoel.statement.mixin.compat1182plus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.collection.IndexedIterable;
import virtuoel.statement.util.StatementRegistryExtensions;

@Mixin(IndexedIterable.class)
public interface IndexedIterableMixin<T> extends StatementRegistryExtensions<T>
{
	@Shadow
	int getRawId(T entry);
	
	@Override
	default int statement_getRawId(T entry)
	{
		return getRawId(entry);
	}
}
