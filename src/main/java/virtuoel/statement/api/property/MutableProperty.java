package virtuoel.statement.api.property;

import java.util.Optional;

import net.minecraft.state.property.Property;

public interface MutableProperty<E extends Comparable<E>>
{
	@SuppressWarnings("unchecked")
	public static <E extends Comparable<E>> Optional<MutableProperty<E>> of(Property<E> property)
	{
		return property instanceof MutableProperty<?> ? Optional.of((MutableProperty<E>) property) : Optional.empty();
	}
	
	@SuppressWarnings("unchecked")
	default Property<E> asProperty()
	{
		return ((Property<E>) this);
	}
}
