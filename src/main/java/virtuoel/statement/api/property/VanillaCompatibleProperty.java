package virtuoel.statement.api.property;

import net.minecraft.state.property.AbstractProperty;

public abstract class VanillaCompatibleProperty<T extends Comparable<T>> extends AbstractProperty<T>
{
	public VanillaCompatibleProperty(String name, Class<T> type)
	{
		super(name, type);
	}
}
