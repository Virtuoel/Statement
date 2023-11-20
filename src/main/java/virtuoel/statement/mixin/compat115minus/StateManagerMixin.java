package virtuoel.statement.mixin.compat115minus;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableMap;

import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import virtuoel.statement.api.RefreshableStateManager;
import virtuoel.statement.util.StatementStateManagerFactoryExtensions;

@Mixin(StateManager.class)
public class StateManagerMixin<O, S extends State<O, S>> implements RefreshableStateManager<O, S>
{
	@Unique StateManager.Factory<O, S> statement_factory;
	@Unique BiFunction<O, ImmutableMap<Property<?>, Comparable<?>>, S> statement_stateFunction;
	
	@SuppressWarnings("unchecked")
	@Inject(at = @At("RETURN"), method = "<init>", remap = false)
	private void onConstruct(Object object, StateManager.Factory<O, S> factory, Map<String, Property<?>> map, CallbackInfo info)
	{
		statement_factory = factory;
		
		statement_stateFunction = ((StatementStateManagerFactoryExtensions<O, S>) factory)::statement_create;
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
}
