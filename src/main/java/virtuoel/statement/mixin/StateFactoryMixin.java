package virtuoel.statement.mixin;

import java.util.HashMap;
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

import net.minecraft.state.PropertyContainer;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import virtuoel.statement.api.RefreshableStateManager;

@Mixin(StateFactory.class)
public class StateFactoryMixin<O, S extends PropertyContainer<S>> implements RefreshableStateManager<O, S>
{
	@Shadow @Final @Mutable ImmutableSortedMap<String, Property<?>> propertyMap;
	@Shadow @Final @Mutable ImmutableList<S> states;
	
	@Unique StateFactory.Factory<O, S, ?> statement_factory;
	@Unique BiFunction<O, ImmutableMap<Property<?>, Comparable<?>>, S> statement_stateFunction;
	
	@SuppressWarnings("unchecked")
	@Inject(at = @At("RETURN"), method = "<init>")
	private void onConstruct(Object object, StateFactory.Factory<O, S, ?> factory, Map<String, Property<?>> map, CallbackInfo info)
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
	public Property<?> statement_addProperty(final Property<?> property)
	{
		final Map<String, Property<?>> map = new HashMap<>(propertyMap);
		final Property<?> ret = map.put(property.getName(), property);
		propertyMap = ImmutableSortedMap.copyOf(map);
		return ret;
	}
	
	@Override
	public Property<?> statement_removeProperty(final String propertyName)
	{
		final Map<String, Property<?>> map = new HashMap<>(propertyMap);
		final Property<?> ret = map.remove(propertyName);
		propertyMap = ImmutableSortedMap.copyOf(map);
		return ret;
	}
	
	@Override
	public boolean statement_removeProperty(final Property<?> property)
	{
		final Map<String, Property<?>> map = new HashMap<>(propertyMap);
		final boolean ret = map.remove(property.getName(), property);
		propertyMap = ImmutableSortedMap.copyOf(map);
		return ret;
	}
}
