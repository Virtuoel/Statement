package virtuoel.statement.api.property;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import net.minecraft.state.property.Property;

public interface MutableProperty<E extends Comparable<E>> extends Collection<E>
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
	
	@Override
	default int size()
	{
		return asProperty().getValues().size();
	}
	
	@Override
	default boolean isEmpty()
	{
		return asProperty().getValues().isEmpty();
	}
	
	@Override
	default boolean contains(Object o)
	{
		return asProperty().getValues().contains(o);
	}
	
	@Override
	default Iterator<E> iterator()
	{
		return asProperty().getValues().iterator();
	}
	
	@Override
	default Object[] toArray()
	{
		return asProperty().getValues().toArray();
	}
	
	@Override
	default <T> T[] toArray(T[] a)
	{
		return asProperty().getValues().toArray(a);
	}
	
	@Override
	default boolean add(E e)
	{
		return asProperty().getValues().add(e);
	}
	
	@Override
	default boolean remove(Object o)
	{
		return asProperty().getValues().remove(o);
	}
	
	@Override
	default boolean containsAll(Collection<?> c)
	{
		return asProperty().getValues().containsAll(c);
	}
	
	@Override
	default boolean addAll(Collection<? extends E> c)
	{
		return asProperty().getValues().addAll(c);
	}
	
	@Override
	default boolean removeAll(Collection<?> c)
	{
		return asProperty().getValues().removeAll(c);
	}
	
	@Override
	default boolean retainAll(Collection<?> c)
	{
		return asProperty().getValues().retainAll(c);
	}
	
	@Override
	default void clear()
	{
		asProperty().getValues().clear();
	}
}
