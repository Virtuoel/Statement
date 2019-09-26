package virtuoel.statement.api;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.MutableTriple;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.PropertyContainer;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.MapUtil;
import virtuoel.statement.api.compatibility.FoamFixCompatibility;

public interface RefreshableStateFactory<O, S extends PropertyContainer<S>>
{
	default BiFunction<O, ImmutableMap<Property<?>, Comparable<?>>, S> statement_getStateFunction()
	{
		return (o, m) -> null;
	}
	
	default Optional<Object> statement_getFactory()
	{
		return Optional.empty();
	}
	
	default void statement_setStates(ImmutableList<S> states)
	{
		
	}
	
	@Nullable
	default Property<?> statement_addProperty(final Property<?> property)
	{
		return null;
	}
	
	@Nullable
	default Property<?> statement_removeProperty(final String propertyName)
	{
		return null;
	}
	
	default boolean statement_removeProperty(final Property<?> property)
	{
		return false;
	}
	
	default Collection<S> statement_refreshPropertyValues(final Property<?> property, final Collection<? extends Comparable<?>> addedValues)
	{
		@SuppressWarnings("unchecked")
		final StateFactory<O, S> self = ((StateFactory<O, S>) (Object) this);
		
		final O baseObject = self.getBaseObject();
		final Collection<Property<?>> properties = self.getProperties();
		final ImmutableList<S> states = self.getStates();
		
		Stream<List<Comparable<?>>> tableStream = Stream.of(Collections.emptyList());
		
		for(final Property<?> entry : properties)
		{
			tableStream = tableStream.flatMap((propertyList) ->
			{
				return entry.getValues().stream().map((val) ->
				{
					final List<Comparable<?>> list = new ArrayList<>(propertyList);
					list.add(val);
					return list;
				});
			});
		}
		
		final Collection<S> currentStates = new LinkedList<>();
		final Collection<S> addedStates = new LinkedList<>();
		
		final Map<Map<Property<?>, Comparable<?>>, S> stateMap = new LinkedHashMap<>();
		
		final BiFunction<O, ImmutableMap<Property<?>, Comparable<?>>, S> function = statement_getStateFunction();
		
		final Optional<MutableTriple<Optional<Field>, Optional<?>, ?>> compatibilityData = FoamFixCompatibility.INSTANCE.resetFactoryMapperData(statement_getFactory());
		
		tableStream.forEach((valueList) ->
		{
			final Map<Property<?>, Comparable<?>> propertyValueMap = MapUtil.createMap(properties, valueList);
			
			final S currentState;
			if(addedValues.contains(propertyValueMap.get(property)))
			{
				currentState = function.apply(baseObject, ImmutableMap.copyOf(propertyValueMap));
				if(currentState != null)
				{
					addedStates.add(currentState);
				}
				
				FoamFixCompatibility.INSTANCE.loadFactoryMapperData(compatibilityData);
			}
			else
			{
				currentState = states.parallelStream().filter(state -> state.getEntries().equals(propertyValueMap)).findFirst().orElse(null);
			}
			
			if(currentState != null)
			{
				stateMap.put(propertyValueMap, currentState);
				currentStates.add(currentState);
			}
		});
		
		if(!addedStates.isEmpty())
		{
			currentStates.parallelStream().forEach(propertyContainer ->
			{
				FoamFixCompatibility.INSTANCE.setStateOwnerData(compatibilityData, propertyContainer);
				
				if(propertyContainer instanceof AbstractPropertyContainer<?, ?>)
				{
					@SuppressWarnings("unchecked")
					final AbstractPropertyContainer<?, S> abstractPropertyContainer = ((AbstractPropertyContainer<?, S>) propertyContainer);
					abstractPropertyContainer.createWithTable(stateMap);
				}
			});
			
			statement_setStates(ImmutableList.copyOf(currentStates));
		}
		
		return addedStates;
	}
}
