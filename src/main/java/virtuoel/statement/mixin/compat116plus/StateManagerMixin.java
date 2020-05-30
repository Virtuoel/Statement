package virtuoel.statement.mixin.compat116plus;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

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
import com.mojang.serialization.MapCodec;

import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import virtuoel.statement.api.RefreshableStateManager;

@Mixin(StateManager.class)
public class StateManagerMixin<O, S extends State<O, S>> implements RefreshableStateManager<O, S>
{
	@Shadow @Final @Mutable ImmutableList<S> states;
	
	@Unique StateManager.Factory<O, S> statement_factory;
	@Unique BiFunction<O, ImmutableMap<Property<?>, Comparable<?>>, S> statement_stateFunction;
	
	@Inject(at = @At("RETURN"), method = "<init>")
	private void onConstruct(Function<O, S> function, Object object, StateManager.Factory<O, S> factory, Map<String, Property<?>> map, CallbackInfo info)
	{
		statement_factory = factory;
		
		@SuppressWarnings("unchecked")
		final MapCodec<S> mapCodec = ((StateAccessor<S>) states.get(0)).getCodec();
		statement_stateFunction = (o, m) -> (S) statement_factory.create(o, m, mapCodec);
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
