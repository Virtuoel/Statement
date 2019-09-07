package virtuoel.statement.api.compatibility;

import java.lang.reflect.Field;
import java.util.Optional;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.state.PropertyContainer;
import net.minecraft.state.property.Property;
import virtuoel.statement.util.FoamFixCompatibilityImpl;

public interface FoamFixCompatibility
{
	public static final FoamFixCompatibility INSTANCE = new FoamFixCompatibilityImpl();
	
	default void removePropertyFromEntryMap(Property<?> property)
	{
		
	}
	
	default Optional<MutableTriple<Optional<Field>, Optional<?>, ?>> resetFactoryMapperData(final Optional<Object> factory)
	{
		return Optional.empty();
	}
	
	default void loadFactoryMapperData(final Optional<MutableTriple<Optional<Field>, Optional<?>, ?>> data)
	{
		
	}
	
	default <T extends Triple<Optional<Field>, Optional<?>, ?>> void setStateOwnerData(final Optional<T> data, final PropertyContainer<?> propertyContainer)
	{
		
	}
}
