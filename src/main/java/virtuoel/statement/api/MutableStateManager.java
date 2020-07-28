package virtuoel.statement.api;

import java.util.Collections;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableSortedMap;

import net.minecraft.state.property.Property;

public interface MutableStateManager
{
	default Map<String, Property<?>> statement_getProperties()
	{
		return Collections.emptyMap();
	}
	
	default void statement_setProperties(Map<String, Property<?>> properties)
	{
		
	}
	
	@Nullable
	default <V extends Comparable<V>> Property<?> statement_addProperty(final Property<V> property, final V defaultValue)
	{
		return statement_addProperty(property);
	}
	
	@Nullable
	default Property<?> statement_addProperty(final Property<?> property)
	{
		final Map<String, Property<?>> properties = statement_getProperties();
		
		final Property<?> ret = properties.get(property.getName());
		
		if (ret != property)
		{
			final ImmutableSortedMap.Builder<String, Property<?>> builder = ImmutableSortedMap.<String, Property<?>>naturalOrder();
			builder.put(property.getName(), property);
			
			if (ret != null)
			{
				for (final Map.Entry<String, Property<?>> e : properties.entrySet())
				{
					final Property<?> value = e.getValue();
					if (value != ret)
					{
						builder.put(e.getKey(), value);
					}
				}
			}
			else
			{
				builder.putAll(properties);
			}
			
			statement_setProperties(builder.build());
		}
		
		return ret;
	}
	
	@Nullable
	default Property<?> statement_removeProperty(final String propertyName)
	{
		final Map<String, Property<?>> properties = statement_getProperties();
		
		final Property<?> ret = properties.get(propertyName);
		
		if (ret != null)
		{
			final ImmutableSortedMap.Builder<String, Property<?>> builder = ImmutableSortedMap.naturalOrder();
			
			for (final Map.Entry<String, Property<?>> e : properties.entrySet())
			{
				final Property<?> value = e.getValue();
				if (value != ret)
				{
					builder.put(e.getKey(), value);
				}
			}
			
			statement_setProperties(builder.build());
		}
		
		return ret;
	}
	
	default boolean statement_removeProperty(final Property<?> property)
	{
		return statement_removeProperty(property.getName()) != null;
	}
}
