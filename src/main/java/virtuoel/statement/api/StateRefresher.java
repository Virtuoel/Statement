package virtuoel.statement.api;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.registry.Registry;
import virtuoel.statement.util.StateRefresherImpl;

public interface StateRefresher
{
	public static final StateRefresher INSTANCE = new StateRefresherImpl();
	
	default <V extends Comparable<V>> Collection<BlockState> addBlockProperty(final Block owner, final Property<V> property, final V defaultValue)
	{
		return addProperty(owner::getStateManager, Block.STATE_IDS, property, defaultValue);
	}
	
	default <V extends Comparable<V>> Collection<FluidState> addFluidProperty(final Fluid owner, final Property<V> property, final V defaultValue)
	{
		return addProperty(owner::getStateManager, Fluid.STATE_IDS, property, defaultValue);
	}
	
	default <O, S extends State<O, S>, V extends Comparable<V>> Collection<S> addProperty(final Supplier<StateManager<O, S>> stateManagerGetter, final IdList<S> idList, final Property<V> property, final V defaultValue)
	{
		return Collections.emptyList();
	}
	
	default <V extends Comparable<V>> Collection<BlockState> removeBlockProperty(final Block owner, final Property<V> property)
	{
		return removeProperty(owner::getStateManager, owner::getDefaultState, property);
	}
	
	default <V extends Comparable<V>> Collection<FluidState> removeFluidProperty(final Fluid owner, final Property<V> property)
	{
		return removeProperty(owner::getStateManager, owner::getDefaultState, property);
	}
	
	default <O, S extends State<O, S>, V extends Comparable<V>> Collection<S> removeProperty(final Supplier<StateManager<O, S>> stateManagerGetter, final Supplier<S> defaultStateGetter, final Property<V> property)
	{
		return Collections.emptyList();
	}
	
	default <V extends Comparable<V>> void refreshBlockStates(final Property<V> property, final Collection<V> addedValues, final Collection<V> removedValues)
	{
		refreshStates(
			Registry.BLOCK, Block.STATE_IDS,
			property, addedValues, removedValues,
			Block::getDefaultState, Block::getStateManager, b -> {}
		);
	}
	
	default <V extends Comparable<V>> void refreshFluidStates(final Property<V> property, final Collection<V> addedValues, final Collection<V> removedValues)
	{
		refreshStates(
			Registry.FLUID, Fluid.STATE_IDS,
			property, addedValues, removedValues,
			Fluid::getDefaultState, Fluid::getStateManager, f -> {}
		);
	}
	
	default <O, V extends Comparable<V>, S extends State<O, S>> void refreshStates(final Iterable<O> registry, final IdList<S> stateIdList, Property<V> property, final Collection<V> addedValues, final Collection<V> removedValues, final Function<O, S> defaultStateGetter, final Function<O, StateManager<O, S>> managerGetter, final Consumer<S> newStateConsumer)
	{
		
	}
	
	default void reorderBlockStates()
	{
		reorderStates(Registry.BLOCK, Block.STATE_IDS, Block::getStateManager);
	}
	
	default void reorderFluidStates()
	{
		reorderStates(Registry.FLUID, Fluid.STATE_IDS, Fluid::getStateManager);
	}
	
	default <O, V extends Comparable<V>, S extends State<O, S>> void reorderStates(final Iterable<O> registry, final IdList<S> stateIdList, final Function<O, StateManager<O, S>> managerGetter)
	{
		
	}
	
	default boolean isParallel()
	{
		return false;
	}
}
