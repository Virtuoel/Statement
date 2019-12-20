package virtuoel.statement.api.compatibility;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Optional;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import virtuoel.statement.util.FoamFixCompatibilityImpl;

public interface FoamFixCompatibility
{
	public static final FoamFixCompatibility INSTANCE = new FoamFixCompatibilityImpl();
	
	default void enable()
	{
		
	}
	
	default void disable()
	{
		
	}
	
	default boolean isEnabled()
	{
		return false;
	}
	
	default void removePropertyFromEntryMap(Property<?> property)
	{
		
	}
	
	default Optional<Object> constructPropertyValueMapper(Collection<Property<?>> properties)
	{
		return Optional.empty();
	}
	
	default void setFactoryMapper(final Optional<?> factory, final Optional<?> mapper)
	{
		
	}
	
	default void setStateOwner(final State<?> state, final Optional<?> owner)
	{
		
	}
	
	@Deprecated
	default Optional<MutableTriple<Optional<Field>, Optional<?>, ?>> resetFactoryMapperData(final Optional<Object> factory)
	{
		return Optional.empty();
	}
	
	@Deprecated
	default void loadFactoryMapperData(final Optional<MutableTriple<Optional<Field>, Optional<?>, ?>> data)
	{
		
	}
	
	@Deprecated
	default <T extends Triple<Optional<Field>, Optional<?>, ?>> void setStateOwnerData(final Optional<T> data, final State<?> state)
	{
		
	}
}
