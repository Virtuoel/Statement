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
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.state.PropertyContainer;
import net.minecraft.state.StateFactory;
import net.minecraft.util.IdList;
import virtuoel.statement.api.ClearableIdList;
import virtuoel.statement.api.MutableProperty;
import virtuoel.statement.api.RefreshableStateFactory;
import virtuoel.statement.api.StateRefresher;
import virtuoel.statement.api.StatementApi;
import virtuoel.statement.api.compatibility.FoamFixCompatibility;

public class StateRefresherImpl implements StateRefresher
{
	public static final Logger LOGGER = LogManager.getLogger(StatementApi.MOD_ID);
	
	private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
	
	@Override
	public <O, V extends Comparable<V>, S extends PropertyContainer<S>> void refreshStates(final Iterable<O> registry, final IdList<S> stateIdList, MutableProperty<V> property, final Collection<V> newValues, final Function<O, S> defaultStateGetter, final Function<O, StateFactory<O, S>> factoryGetter, final Consumer<S> newStateConsumer)
	{
		final long startTime = System.nanoTime();
		
		final List<RefreshableStateFactory<O, S>> factoriesToRefresh = new LinkedList<>();
		
		for(final O entry : registry)
		{
			if(defaultStateGetter.apply(entry).getEntries().containsKey(property))
			{
				@SuppressWarnings("unchecked")
				final RefreshableStateFactory<O, S> factory = (RefreshableStateFactory<O, S>) factoryGetter.apply(entry);
				
				factoriesToRefresh.add(factory);
			}
		}
		
		final int entryQuantity = factoriesToRefresh.size();
		
		final Collection<S> newStates = new ConcurrentLinkedQueue<>();
		
		final Collection<CompletableFuture<?>> allFutures = new LinkedList<>();
		
		LOGGER.debug("Refreshing states of {} entries for values(s) {} after {} ns of setup.", entryQuantity, newValues, System.nanoTime() - startTime);
		
		synchronized(property)
		{
			newValues.forEach(property::addValue);
			
			FoamFixCompatibility.INSTANCE.removePropertyFromEntryMap(property);
		}
		
		synchronized(stateIdList)
		{
			for(final RefreshableStateFactory<O, S> factory : factoriesToRefresh)
			{
				allFutures.add(CompletableFuture.supplyAsync(() ->
				{
					return factory.statement_refreshPropertyValues(property, newValues);
				},
				EXECUTOR).thenAccept(newStates::addAll));
			}
			
			CompletableFuture.allOf(allFutures.stream().toArray(CompletableFuture<?>[]::new))
			.thenAccept(v ->
			{
				newStates.forEach(state ->
				{
					newStateConsumer.accept(state);
					stateIdList.add(state);
				});
				
				LOGGER.debug("Added {} new states for values(s) {} after {} ms.", newStates.size(), newValues, (System.nanoTime() - startTime) / 1_000_000);
			}).join();
		}
	}
	
	public static final Predicate<PropertyContainer<?>> DEFERRED_STATE_PREDICATE = state -> StatementApi.ENTRYPOINTS.stream().anyMatch(api -> api.shouldDeferProperty(state));
	
	@Override
	public <O, V extends Comparable<V>, S extends PropertyContainer<S>> void reorderStates(final Iterable<O> registry, final IdList<S> stateIdList, final Function<O, StateFactory<O, S>> factoryGetter)
	{
		((ClearableIdList) stateIdList).statement_clear();
		
		final Collection<S> allStates = new LinkedList<>();
		
		for(final O entry : registry)
		{
			factoryGetter.apply(entry).getStates().forEach(allStates::add);
		}
		
		final Collection<S> deferredStates = new LinkedList<>();
		
		for(final S state : allStates)
		{
			if(DEFERRED_STATE_PREDICATE.test(state))
			{
				deferredStates.add(state);
			}
			else
			{
				stateIdList.add(state);
			}
		}
		
		deferredStates.forEach(stateIdList::add);
	}
}
