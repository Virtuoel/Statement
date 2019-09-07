package virtuoel.statement;

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

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.impl.registry.RemovableIdList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.PropertyContainer;
import net.minecraft.state.StateFactory;
import net.minecraft.util.IdList;
import net.minecraft.util.registry.Registry;
import virtuoel.statement.api.MutableProperty;
import virtuoel.statement.api.RefreshableStateFactory;
import virtuoel.statement.api.compatibility.FoamFixCompatibility;

public class Statement implements ModInitializer
{
	public static final String MOD_ID = "statement";
	
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	
	private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
	
	@Override
	public void onInitialize()
	{
		
	}
	
	public static <V extends Comparable<V>> void refreshBlockStates(final MutableProperty<V> property, final Collection<V> newValues)
	{
		refreshStates(
			Registry.BLOCK, Block.STATE_IDS,
			property, newValues,
			Block::getDefaultState, Block::getStateFactory, BlockState::initShapeCache
		);
	}
	
	public static <V extends Comparable<V>> void refreshFluidStates(final MutableProperty<V> property, final Collection<V> newValues)
	{
		refreshStates(
			Registry.FLUID, Fluid.STATE_IDS,
			property, newValues,
			Fluid::getDefaultState, Fluid::getStateFactory, f -> {}
		);
	}
	
	public static <O, V extends Comparable<V>, S extends PropertyContainer<S>> void refreshStates(final Iterable<O> registry, final IdList<S> stateIdList, MutableProperty<V> property, final Collection<V> newValues, final Function<O, S> defaultStateGetter, final Function<O, StateFactory<O, S>> factoryGetter, final Consumer<S> newStateConsumer)
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
	
	public static void reorderBlockStates(final Predicate<BlockState> deferredCondition)
	{
		reorderStates(Registry.BLOCK, Block.STATE_IDS, Block::getStateFactory, deferredCondition);
	}
	
	public static void reorderFluidStates(final Predicate<FluidState> deferredCondition)
	{
		reorderStates(Registry.FLUID, Fluid.STATE_IDS, Fluid::getStateFactory, deferredCondition);
	}
	
	public static <O, V extends Comparable<V>, S extends PropertyContainer<S>> void reorderStates(final Iterable<O> registry, final IdList<S> stateIdList, final Function<O, StateFactory<O, S>> factoryGetter, final Predicate<S> deferredCondition)
	{
		@SuppressWarnings("unchecked")
		final RemovableIdList<S> removableIdList = ((RemovableIdList<S>) stateIdList);
		removableIdList.fabric_clear();
		
		final Collection<S> allStates = new LinkedList<>();
		
		for(final O entry : registry)
		{
			factoryGetter.apply(entry).getStates().forEach(allStates::add);
		}
		
		final Collection<S> deferredStates = new LinkedList<>();
		
		for(final S state : allStates)
		{
			if(deferredCondition.test(state))
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
