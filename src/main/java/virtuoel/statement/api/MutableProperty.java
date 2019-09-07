package virtuoel.statement.api;

import net.minecraft.state.property.Property;

public interface MutableProperty<T extends Comparable<T>> extends Property<T>
{
	void addValue(T value);
	
	T removeValue(T value);
}
