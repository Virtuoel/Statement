package virtuoel.statement.api.property;

import net.minecraft.state.property.Property;

public abstract class VanillaCompatibleProperty<T extends Comparable<T>> extends Property<T>
{
	public VanillaCompatibleProperty(String name, Class<T> type)
	{
		super(name, type);
	}
}
