package virtuoel.statement.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.state.PropertyContainer;
import net.minecraft.state.StateFactory;
import net.minecraft.util.IdList;
import virtuoel.statement.api.ClearableIdList;
import virtuoel.statement.api.MutableProperty;
import virtuoel.statement.api.MutableStateFactory;
import virtuoel.statement.api.StateRefresher;
import virtuoel.statement.api.StatementApi;
import virtuoel.statement.api.compatibility.FoamFixCompatibility;

public class StateRefresherImpl implements StateRefresher
{
	private static final Logger LOGGER = LogManager.getLogger(StatementApi.MOD_ID);
	
	private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
	
	@Override
	public <O, V extends Comparable<V>, S extends PropertyContainer<S>> void refreshStates(final Iterable<O> registry, final IdList<S> stateIdList, MutableProperty<V> property, final Collection<V> addedValues, final Collection<V> removedValues, final Function<O, S> defaultStateGetter, final Function<O, StateFactory<O, S>> factoryGetter, final Consumer<S> newStateConsumer)
	{
		final long startTime = System.nanoTime();
		
		final List<MutableStateFactory<O, S>> factoriesToRefresh = new LinkedList<>();
		
		for(final O entry : registry)
		{
			if(defaultStateGetter.apply(entry).getEntries().containsKey(property))
			{
				@SuppressWarnings("unchecked")
				final MutableStateFactory<O, S> factory = (MutableStateFactory<O, S>) factoryGetter.apply(entry);
				
				factoriesToRefresh.add(factory);
			}
		}
		
		final Collection<S> addedStates = new ConcurrentLinkedQueue<>();
		final Collection<S> removedStates = new ConcurrentLinkedQueue<>();
		
		final Collection<CompletableFuture<?>> allFutures = new LinkedList<>();
		
		final int entryQuantity = factoriesToRefresh.size();
		
		final int addedValueQuantity = addedValues.size();
		final int removedValueQuantity = removedValues.size();
		
		final boolean noAddedValues = addedValueQuantity == 0;
		final boolean noRemovedValues = removedValueQuantity == 0;
		
		if(noAddedValues && noRemovedValues)
		{
			LOGGER.debug("Refreshing states of {} entries after {} ns of setup.", entryQuantity, System.nanoTime() - startTime);
		}
		else if(noAddedValues || noRemovedValues)
		{
			LOGGER.debug("Refreshing states of {} entries for {} values(s) {} after {} ns of setup.", entryQuantity, noRemovedValues ? "new" : "removed", noRemovedValues ? addedValues : removedValues, System.nanoTime() - startTime);
		}
		else
		{
			LOGGER.debug("Refreshing states of {} entries for new values(s) {} and removed value(s) {} after {} ns of setup.", entryQuantity, addedValues, removedValues, System.nanoTime() - startTime);
		}
		
		synchronized(property)
		{
			property.addAll(addedValues);
			property.removeAll(removedValues);
			
			FoamFixCompatibility.INSTANCE.removePropertyFromEntryMap(property);
		}
		
		synchronized(stateIdList)
		{
			for(final MutableStateFactory<O, S> factory : factoriesToRefresh)
			{
				allFutures.add(CompletableFuture.supplyAsync(() ->
				{
					if(!noRemovedValues)
					{
						@SuppressWarnings("unchecked")
						final StateFactory<O, S> f = ((StateFactory<O, S>) factory);
						f.getStates().parallelStream().filter(state -> state.getEntries().containsKey(property) && removedValues.contains(state.get(property))).forEach(removedStates::add);
					}
					
					return factory.statement_refreshPropertyValues(property, addedValues);
				},
				EXECUTOR).thenAccept(addedStates::addAll));
			}
			
			CompletableFuture.allOf(allFutures.stream().toArray(CompletableFuture<?>[]::new))
			.thenAccept(v ->
			{
				addedStates.forEach(state ->
				{
					newStateConsumer.accept(state);
					stateIdList.add(state);
				});
				
				final int addedStateQuantity = addedStates.size();
				final int removedStateQuantity = removedStates.size();
				
				final boolean noAdditions = addedStateQuantity == 0;
				final boolean noRemovals = removedStateQuantity == 0;
				
				if(noAdditions && noRemovals)
				{
					LOGGER.debug("Refreshed states with no additions or removals after {} ms.", System.nanoTime() - startTime);
				}
				else if(noAdditions || noRemovals)
				{
					LOGGER.debug("{} {} state(s) for {} values(s) {} after {} ms.", noRemovals ? "Added" : "Removed", noRemovals ? addedStateQuantity : removedStateQuantity, noRemovals ? "new" : "old", noRemovals ? addedValues : removedValues, (System.nanoTime() - startTime) / 1_000_000);
				}
				else
				{
					LOGGER.debug("Added {} state(s) for new values(s) {} and removed {} states for old value(s) {} after {} ms.", addedStateQuantity, addedValues, removedStateQuantity, removedValues, (System.nanoTime() - startTime) / 1_000_000);
				}
			}).join();
		}
	}
	
	@Override
	public <O, V extends Comparable<V>, S extends PropertyContainer<S>> void reorderStates(final Iterable<O> registry, final IdList<S> stateIdList, final Function<O, StateFactory<O, S>> factoryGetter)
	{
		final Collection<S> allStates = new LinkedList<>();
		
		for(final O entry : registry)
		{
			factoryGetter.apply(entry).getStates().forEach(allStates::add);
		}
		
		final Collection<S> initialStates = new LinkedList<>();
		final Collection<S> deferredStates = new LinkedList<>();
		
		for(final S state : allStates)
		{
			if(StatementApi.ENTRYPOINTS.stream().anyMatch(api -> api.shouldDeferState(state)))
			{
				deferredStates.add(state);
			}
			else
			{
				initialStates.add(state);
			}
		}
		
		((ClearableIdList) stateIdList).statement_clear();
		initialStates.forEach(stateIdList::add);
		deferredStates.forEach(stateIdList::add);
	}
}
