package virtuoel.statement.api;

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

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import virtuoel.statement.api.compatibility.FoamFixCompatibility;
import virtuoel.statement.util.StatementPropertyExtensions;
import virtuoel.statement.util.StatementStateExtensions;

public interface RefreshableStateManager<O, S extends State<O, S>> extends MutableStateManager
{
	default BiFunction<O, ImmutableMap<Property<?>, Comparable<?>>, S> statement_getStateFunction()
	{
		return (o, m) -> null;
	}
	
	default Optional<Object> statement_getFactory()
	{
		return Optional.empty();
	}
	
	default void statement_setStateList(ImmutableList<S> states)
	{
		
	}
	
	default void statement_rebuildCodec()
	{
		
	}
	
	default <V extends Comparable<V>> Collection<S> statement_reconstructStateList(final Map<Property<V>, Collection<V>> addedValueMap)
	{
		@SuppressWarnings("unchecked")
		final StateManager<O, S> self = ((StateManager<O, S>) (Object) this);
		
		final O owner = self.getOwner();
		final Collection<Property<?>> properties = self.getProperties();
		final ImmutableList<S> states = self.getStates();
		
		Stream<List<Pair<Property<?>, Comparable<?>>>> tableStream = Stream.of(Collections.emptyList());
		
		for (final Property<?> entry : properties)
		{
			final Collection<V> deferred = addedValueMap.getOrDefault(entry, new LinkedList<>());
			tableStream = tableStream.flatMap((propertyList) ->
			{
				@SuppressWarnings("unchecked")
				final StatementPropertyExtensions<Comparable<?>> p = ((StatementPropertyExtensions<Comparable<?>>) entry);
				
				final List<List<Pair<Property<?>, Comparable<?>>>> values = new ArrayList<>();
				final List<List<Pair<Property<?>, Comparable<?>>>> addedValues = new ArrayList<>();
				
				for (final Comparable<?> val : p.statement_getValues())
				{
					final List<Pair<Property<?>, Comparable<?>>> list = new ArrayList<>(propertyList);
					list.add(Pair.of(entry, val));
					
					(deferred.contains(val) ? addedValues : values).add(list);
				}
				
				values.addAll(addedValues);
				
				return values.stream();
			});
		}
		
		final Collection<S> currentStates = new LinkedList<>();
		final Collection<S> addedStates = new LinkedList<>();
		
		final Map<Map<Property<?>, Comparable<?>>, S> stateMap = new LinkedHashMap<>();
		
		statement_rebuildCodec();
		
		final BiFunction<O, ImmutableMap<Property<?>, Comparable<?>>, S> function = statement_getStateFunction();
		
		final Optional<?> mapper = FoamFixCompatibility.INSTANCE.constructPropertyValueMapper(properties);
		
		FoamFixCompatibility.INSTANCE.setFactoryMapper(statement_getFactory(), mapper);
		
		tableStream.forEach((valueList) ->
		{
			final ImmutableMap<Property<?>, Comparable<?>> propertyValueMap = valueList.stream().collect(ImmutableMap.toImmutableMap(Pair::getLeft, Pair::getRight));
			
			final S currentState;
			if (addedValueMap.entrySet().stream().anyMatch(e -> e.getValue().contains(propertyValueMap.get(e.getKey()))))
			{
				currentState = function.apply(owner, propertyValueMap);
				if (currentState != null)
				{
					addedStates.add(currentState);
				}
			}
			else
			{
				currentState = (StateRefresher.INSTANCE.isParallel() ? states.parallelStream() : states.stream()).filter(state -> ((StatementStateExtensions<?>) state).statement_getEntries().equals(propertyValueMap)).findFirst().orElse(null);
			}
			
			if (currentState != null)
			{
				stateMap.put(propertyValueMap, currentState);
				currentStates.add(currentState);
			}
		});
		
		final Stream<S> stateStream = (StateRefresher.INSTANCE.isParallel() ? currentStates.parallelStream() : currentStates.stream());
		if (!addedStates.isEmpty())
		{
			stateStream.forEach(propertyContainer ->
			{
				FoamFixCompatibility.INSTANCE.setStateOwner(propertyContainer, mapper);
				
				StatementStateExtensions.statement_cast(propertyContainer).statement_createWithTable(stateMap);
			});
			
			statement_setStateList(ImmutableList.copyOf(currentStates));
		}
		else if (FoamFixCompatibility.INSTANCE.isEnabled())
		{
			stateStream.forEach(propertyContainer ->
			{
				FoamFixCompatibility.INSTANCE.setStateOwner(propertyContainer, mapper);
			});
		}
		
		return addedStates;
	}
}
