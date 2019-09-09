package virtuoel.statement.api;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.state.PropertyContainer;
import net.minecraft.state.StateFactory;
import net.minecraft.util.IdList;
import net.minecraft.util.registry.Registry;
import virtuoel.statement.util.StateRefresherImpl;

public interface StateRefresher
{
	public static final StateRefresher INSTANCE = new StateRefresherImpl();
	
	default <V extends Comparable<V>> void refreshBlockStates(final MutableProperty<V> property, final Collection<V> newValues)
	{
		refreshStates(
			Registry.BLOCK, Block.STATE_IDS,
			property, newValues,
			Block::getDefaultState, Block::getStateFactory, BlockState::initShapeCache
		);
	}
	
	default <V extends Comparable<V>> void refreshFluidStates(final MutableProperty<V> property, final Collection<V> newValues)
	{
		refreshStates(
			Registry.FLUID, Fluid.STATE_IDS,
			property, newValues,
			Fluid::getDefaultState, Fluid::getStateFactory, f -> {}
		);
	}
	
	default <O, V extends Comparable<V>, S extends PropertyContainer<S>> void refreshStates(final Iterable<O> registry, final IdList<S> stateIdList, MutableProperty<V> property, final Collection<V> newValues, final Function<O, S> defaultStateGetter, final Function<O, StateFactory<O, S>> factoryGetter, final Consumer<S> newStateConsumer)
	{
		
	}
	
	default void reorderBlockStates()
	{
		reorderStates(Registry.BLOCK, Block.STATE_IDS, Block::getStateFactory);
	}
	
	default void reorderFluidStates()
	{
		reorderStates(Registry.FLUID, Fluid.STATE_IDS, Fluid::getStateFactory);
	}
	
	default <O, V extends Comparable<V>, S extends PropertyContainer<S>> void reorderStates(final Iterable<O> registry, final IdList<S> stateIdList, final Function<O, StateFactory<O, S>> factoryGetter)
	{
		
	}
}