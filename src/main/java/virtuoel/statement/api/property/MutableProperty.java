package virtuoel.statement.api.property;

import java.util.Collection;
import java.util.Iterator;

import net.minecraft.state.property.Property;

public interface MutableProperty<E extends Comparable<E>> extends Property<E>, Collection<E>
{
	@Override
	default int size()
	{
		return getValues().size();
	}
	
	@Override
	default boolean isEmpty()
	{
		return getValues().isEmpty();
	}
	
	@Override
	default boolean contains(Object o)
	{
		return getValues().contains(o);
	}
	
	@Override
	default Iterator<E> iterator()
	{
		return getValues().iterator();
	}
	
	@Override
	default Object[] toArray()
	{
		return getValues().toArray();
	}
	
	@Override
	default <T> T[] toArray(T[] a)
	{
		return getValues().toArray(a);
	}
	
	@Override
	default boolean add(E e)
	{
		return getValues().add(e);
	}
	
	@Override
	default boolean remove(Object o)
	{
		return getValues().remove(o);
	}
	
	@Override
	default boolean containsAll(Collection<?> c)
	{
		return getValues().containsAll(c);
	}
	
	@Override
	default boolean addAll(Collection<? extends E> c)
	{
		return getValues().addAll(c);
	}
	
	@Override
	default boolean removeAll(Collection<?> c)
	{
		return getValues().removeAll(c);
	}
	
	@Override
	default boolean retainAll(Collection<?> c)
	{
		return getValues().retainAll(c);
	}
	
	@Override
	default void clear()
	{
		getValues().clear();
	}
}
