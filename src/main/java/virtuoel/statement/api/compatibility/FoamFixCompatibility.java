package virtuoel.statement.api.compatibility;

import java.util.Collection;
import java.util.Optional;

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
}
