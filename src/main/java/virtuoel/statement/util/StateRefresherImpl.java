package virtuoel.statement.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.registry.Registry;
import virtuoel.statement.Statement;
import virtuoel.statement.api.ClearableIdList;
import virtuoel.statement.api.RefreshableStateManager;
import virtuoel.statement.api.StateRefresher;
import virtuoel.statement.api.StatementApi;
import virtuoel.statement.api.StatementConfig;
import virtuoel.statement.api.compatibility.FoamFixCompatibility;
import virtuoel.statement.api.property.MutableProperty;

public class StateRefresherImpl implements StateRefresher
{
	private static final Logger LOGGER = LogManager.getLogger(StatementApi.MOD_ID);
	
	private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
	
	@Override
	public <O, S extends State<O, S>, V extends Comparable<V>> Collection<S> addProperty(final Supplier<StateManager<O, S>> stateManagerGetter, final IdList<S> idList, final Property<V> property, final V defaultValue)
	{
		@SuppressWarnings("unchecked")
		final RefreshableStateManager<O, S> manager = ((RefreshableStateManager<O, S>) stateManagerGetter.get());
		
		manager.statement_addProperty(property, defaultValue);
		
		@SuppressWarnings("unchecked")
		final StatementPropertyExtensions<V> p = (StatementPropertyExtensions<V>) property;
		final List<V> nonDefaultValues = p.statement_getValues().stream().filter(v -> v != defaultValue).collect(Collectors.toList());
		
		final Collection<S> states = manager.statement_reconstructStateList(Collections.singletonMap(property, nonDefaultValues));
		
		for (final S s : states)
		{
			idList.add(s);
			((StatementStateExtensions<?>) s).statement_initShapeCache();
		}
		
		return states;
	}
	
	@Override
	public <O, S extends State<O, S>, V extends Comparable<V>> Collection<S> removeProperty(final Supplier<StateManager<O, S>> stateManagerGetter, final Supplier<S> defaultStateGetter, final Property<V> property)
	{
		final StateManager<O, S> stateManager = stateManagerGetter.get();
		
		@SuppressWarnings("unchecked")
		final RefreshableStateManager<O, S> manager = ((RefreshableStateManager<O, S>) stateManager);
		
		final Property<?> named = stateManager.getProperty(((StatementPropertyExtensions<?>) property).statement_getName());
		
		if (named != null)
		{
			final S defaultState = defaultStateGetter.get();
			
			if (defaultState.getEntries().containsKey(named))
			{
				final Object defaultValue = defaultState.get(named);
				
				final Collection<S> states = stateManager.getStates().stream().filter(s -> s.get(named) != defaultValue).collect(Collectors.toList());
				
				if (manager.statement_removeProperty(named))
				{
					manager.statement_reconstructStateList(Collections.emptyMap());
				}
				
				return states;
			}
		}
		
		return Collections.emptyList();
	}
	
	@Override
	public <V extends Comparable<V>> void refreshBlockStates(Property<V> property, Collection<V> addedValues, Collection<V> removedValues)
	{
		refreshStates(
			Registry.BLOCK, Block.STATE_IDS,
			property, addedValues, removedValues,
			Block::getDefaultState, Block::getStateManager, s -> ((StatementBlockStateExtensions) s).statement_initShapeCache()
		);
		
		Statement.markRegistryAsModded(Registry.BLOCK);
	}
	
	@Override
	public <V extends Comparable<V>> void refreshFluidStates(final Property<V> property, final Collection<V> addedValues, final Collection<V> removedValues)
	{
		refreshStates(
			Registry.FLUID, Fluid.STATE_IDS,
			property, addedValues, removedValues,
			Fluid::getDefaultState, Fluid::getStateManager, f -> {}
		);
		
		Statement.markRegistryAsModded(Registry.FLUID);
	}
	
	@Override
	public <O, V extends Comparable<V>, S extends State<O, S>> void refreshStates(final Iterable<O> registry, final IdList<S> stateIdList, Property<V> property, final Collection<V> addedValues, final Collection<V> removedValues, final Function<O, S> defaultStateGetter, final Function<O, StateManager<O, S>> stateManagerGetter, final Consumer<S> newStateConsumer)
	{
		Statement.invalidateCustomStateData(stateIdList);
		
		final long startTime = System.nanoTime();
		
		final List<RefreshableStateManager<O, S>> managersToRefresh = new LinkedList<>();
		
		for (final O entry : registry)
		{
			if (((StatementStateExtensions<?>) defaultStateGetter.apply(entry)).statement_getEntries().containsKey(property))
			{
				@SuppressWarnings("unchecked")
				final RefreshableStateManager<O, S> manager = (RefreshableStateManager<O, S>) stateManagerGetter.apply(entry);
				
				managersToRefresh.add(manager);
			}
		}
		
		final Map<Property<V>, Collection<V>> addedValueMap = new HashMap<>();
		
		final Collection<S> addedStates = new ConcurrentLinkedQueue<>();
		final Collection<S> removedStates = new ConcurrentLinkedQueue<>();
		
		final Collection<CompletableFuture<?>> allFutures = new LinkedList<>();
		
		final int entryQuantity = managersToRefresh.size();
		
		final int addedValueQuantity = addedValues.size();
		final int removedValueQuantity = removedValues.size();
		
		final boolean noAddedValues = addedValueQuantity == 0;
		final boolean noRemovedValues = removedValueQuantity == 0;
		
		if (noAddedValues && noRemovedValues)
		{
			LOGGER.debug("Refreshing states of {} entries after {} ns of setup.", entryQuantity, System.nanoTime() - startTime);
		}
		else if (noAddedValues || noRemovedValues)
		{
			LOGGER.debug("Refreshing states of {} entries for {} values(s) {} after {} ns of setup.", entryQuantity, noRemovedValues ? "new" : "removed", noRemovedValues ? addedValues : removedValues, System.nanoTime() - startTime);
		}
		else
		{
			LOGGER.debug("Refreshing states of {} entries for new values(s) {} and removed value(s) {} after {} ns of setup.", entryQuantity, addedValues, removedValues, System.nanoTime() - startTime);
		}
		
		synchronized (property)
		{
			MutableProperty.of(property).ifPresent(mutableProperty ->
			{
				addedValueMap.put(property, addedValues);
				
				@SuppressWarnings("unchecked")
				final StatementPropertyExtensions<V> p = (StatementPropertyExtensions<V>) mutableProperty;
				final Collection<V> values = p.statement_getValues();
				values.addAll(addedValues);
				values.removeAll(removedValues);
				
				FoamFixCompatibility.INSTANCE.removePropertyFromEntryMap(property);
			});
		}
		
		synchronized (stateIdList)
		{
			for (final RefreshableStateManager<O, S> manager : managersToRefresh)
			{
				allFutures.add(CompletableFuture.supplyAsync(() ->
				{
					if (!noRemovedValues)
					{
						@SuppressWarnings("unchecked")
						final StateManager<O, S> f = ((StateManager<O, S>) manager);
						f.getStates().parallelStream().filter(state -> state.getEntries().containsKey(property) && removedValues.contains(state.get(property))).forEach(removedStates::add);
					}
					
					return manager.statement_reconstructStateList(addedValueMap);
				}, EXECUTOR).thenAccept(addedStates::addAll));
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
				
				if (noAdditions && noRemovals)
				{
					LOGGER.debug("Refreshed states with no additions or removals after {} ms.", System.nanoTime() - startTime);
				}
				else if (noAdditions || noRemovals)
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
	public <O, V extends Comparable<V>, S extends State<O, S>> void reorderStates(final Iterable<O> registry, final IdList<S> stateIdList, final Function<O, StateManager<O, S>> stateManagerGetter)
	{
		final Iterable<O> entries;
		
		if (registry instanceof Registry)
		{
			@SuppressWarnings("unchecked")
			final StatementRegistryExtensions<O> reg = (StatementRegistryExtensions<O>) registry;
			final Int2ObjectMap<O> sortedEntries = new Int2ObjectRBTreeMap<>();
			
			for (final O entry : registry)
			{
				sortedEntries.put(reg.statement_getRawId(entry), entry);
			}
			
			entries = sortedEntries.values();
		}
		else
		{
			entries = registry;
		}
		
		final Collection<S> initialStates = new LinkedList<>();
		final Collection<S> deferredStates = new LinkedList<>();
		
		for (final O entry : entries)
		{
			for (final S state : stateManagerGetter.apply(entry).getStates())
			{
				if (Statement.shouldStateBeDeferred(stateIdList, state))
				{
					deferredStates.add(state);
				}
				else
				{
					initialStates.add(state);
				}
			}
		}
		
		((ClearableIdList) stateIdList).statement_clear();
		initialStates.forEach(stateIdList::add);
		deferredStates.forEach(stateIdList::add);
	}
	
	private Boolean parallel = null;
	
	@Override
	public boolean isParallel()
	{
		if (parallel == null)
		{
			final boolean forceParallelMode = StatementConfig.COMMON.forceParallelMode.get();
			
			final boolean ferriteCoreLoaded = ModLoaderUtils.isModLoaded("ferritecore");
			
			parallel = forceParallelMode || !ferriteCoreLoaded;
		}
		
		return parallel;
	}
}
