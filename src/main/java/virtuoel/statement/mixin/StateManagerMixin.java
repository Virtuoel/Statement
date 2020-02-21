package virtuoel.statement.mixin;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import virtuoel.statement.api.RefreshableStateManager;

@Mixin(StateManager.class)
public class StateManagerMixin<O, S extends State<S>> implements RefreshableStateManager<O, S>
{
	@Shadow @Final @Mutable ImmutableSortedMap<String, Property<?>> properties;
	@Shadow @Final @Mutable ImmutableList<S> states;
	
	@Unique StateManager.Factory<O, S, ?> statement_factory;
	@Unique BiFunction<O, ImmutableMap<Property<?>, Comparable<?>>, S> statement_stateFunction;
	
	@SuppressWarnings("unchecked")
	@Inject(at = @At("RETURN"), method = "<init>")
	private void onConstruct(Object object, StateManager.Factory<O, S, ?> factory, Map<String, Property<?>> map, CallbackInfo info)
	{
		statement_factory = factory;
		statement_stateFunction = (o, m) -> (S) statement_factory.create(o, m);
	}
	
	@Override
	public Optional<Object> statement_getFactory()
	{
		return Optional.of(statement_factory);
	}
	
	@Override
	public BiFunction<O, ImmutableMap<Property<?>, Comparable<?>>, S> statement_getStateFunction()
	{
		return statement_stateFunction;
	}
	
	@Override
	public void statement_setStateList(ImmutableList<S> states)
	{
		this.states = states;
	}
	
	@Override
	public Map<String, Property<?>> statement_getProperties()
	{
		return properties;
	}
	
	@Override
	public void statement_setProperties(Map<String, Property<?>> properties)
	{
		if (properties instanceof ImmutableSortedMap<?, ?>)
		{
			this.properties = (ImmutableSortedMap<String, Property<?>>) properties;
		}
	}
}
