package virtuoel.statement.api;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.util.IdList;
import net.minecraft.util.registry.Registry;
import virtuoel.statement.api.property.MutableProperty;
import virtuoel.statement.util.StateRefresherImpl;

public interface StateRefresher
{
	public static final StateRefresher INSTANCE = new StateRefresherImpl();
	
	default <V extends Comparable<V>> void refreshBlockStates(final MutableProperty<V> property, final Collection<V> addedValues, final Collection<V> removedValues)
	{
		refreshStates(
			Registry.BLOCK, Block.STATE_IDS,
			property, addedValues, removedValues,
			Block::getDefaultState, Block::getStateManager, BlockState::initShapeCache
		);
	}
	
	default <V extends Comparable<V>> void refreshFluidStates(final MutableProperty<V> property, final Collection<V> addedValues, final Collection<V> removedValues)
	{
		refreshStates(
			Registry.FLUID, Fluid.STATE_IDS,
			property, addedValues, removedValues,
			Fluid::getDefaultState, Fluid::getStateManager, f -> {}
		);
	}
	
	default <O, V extends Comparable<V>, S extends State<S>> void refreshStates(final Iterable<O> registry, final IdList<S> stateIdList, MutableProperty<V> property, final Collection<V> addedValues, final Collection<V> removedValues, final Function<O, S> defaultStateGetter, final Function<O, StateManager<O, S>> managerGetter, final Consumer<S> newStateConsumer)
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
	
	default <O, V extends Comparable<V>, S extends State<S>> void reorderStates(final Iterable<O> registry, final IdList<S> stateIdList, final Function<O, StateManager<O, S>> managerGetter)
	{
		
	}
	
	@Deprecated
	default <O, T> boolean provideTask(final O object, final Function<O, Consumer<T>> taskConsumerFunction, final Function<StateRefresher, T> task)
	{
		taskConsumerFunction.apply(object).accept(task.apply(this));
		
		return true;
	}
}
