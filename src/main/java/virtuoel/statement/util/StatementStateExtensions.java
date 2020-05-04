package virtuoel.statement.util;

import net.minecraft.state.property.Property;

public interface StatementStateExtensions
{
	default <V extends Comparable<V>> boolean statement_addEntry(final Property<V> property, final V value)
	{
		return false;
	}
}
