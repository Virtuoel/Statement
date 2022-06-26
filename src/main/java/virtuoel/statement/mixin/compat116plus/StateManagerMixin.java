package virtuoel.statement.mixin.compat116plus;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.OptionalFieldCodec;

import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import virtuoel.statement.api.RefreshableStateManager;
import virtuoel.statement.util.StatementStateExtensions;

@Mixin(StateManager.class)
public class StateManagerMixin<O, S extends State<O, S>> implements RefreshableStateManager<O, S>
{
	@Shadow @Final @Mutable O owner;
	@Shadow @Final @Mutable ImmutableSortedMap<String, Property<?>> properties;
	@Shadow @Final @Mutable ImmutableList<S> states;
	
	@Shadow
	private static <S extends State<?, S>, T extends Comparable<T>> MapCodec<S> method_30040(MapCodec<S> mapCodec, Supplier<S> supplier, String string, Property<T> property)
	{
		return null;
	}
	
	@Unique StateManager.Factory<O, S> statement_factory;
	@Unique BiFunction<O, ImmutableMap<Property<?>, Comparable<?>>, S> statement_stateFunction;
	@Unique MapCodec<S> mapCodec;
	@Unique Supplier<S> decoder;
	
	@Inject(at = @At("RETURN"), method = "<init>")
	private void onConstruct(Function<O, S> function, Object object, StateManager.Factory<O, S> factory, Map<String, Property<?>> map, CallbackInfo info)
	{
		statement_factory = factory;
		
		this.decoder = () -> function.apply(owner);
		
		@SuppressWarnings("unchecked")
		final MapCodec<S> c = (MapCodec<S>) ((StatementStateExtensions<S>) states.get(0)).statement_getCodec();
		mapCodec = c;
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
	
	@Override
	public void statement_rebuildCodec()
	{
		MapCodec<S> mapCodec = MapCodec.of(Encoder.empty(), Decoder.unit(decoder));
		
		for (final Entry<String, Property<?>> entry : this.properties.entrySet())
		{
			mapCodec = method_30040(mapCodec, decoder, entry.getKey(), entry.getValue());
		}
		
		this.mapCodec = (MapCodec<S>) mapCodec;
		
		StatementStateExtensions<S> s;
		for (final S state : states)
		{
			s = StatementStateExtensions.statement_cast(state);
			s.statement_setCodec(this.mapCodec);
		}
	}
	
	@Redirect(method = "method_30040", require = 0, at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;fieldOf(Ljava/lang/String;)Lcom/mojang/serialization/MapCodec;"))
	private static <S extends State<?, S>, T extends Comparable<T>> MapCodec<Property.Value<T>> fieldOfProxy(Codec<Property.Value<T>> c, String string, MapCodec<S> mapCodec, Supplier<S> supplier, String noop, Property<T> arg)
	{
		final Supplier<Property.Value<T>> v = Suppliers.memoize(() -> arg.createValue(supplier.get()));
		return new OptionalFieldCodec<>(string, c).xmap(
			o -> o.orElse(v.get()),
			a -> Objects.equals(a, v.get()) ? Optional.empty() : Optional.of(a)
		);
	}
	
	@Inject(at = @At("HEAD"), cancellable = true, method = "method_30039", require = 0)
	private static <S extends State<?, S>, T extends Comparable<T>> void int_onSetPartial(Property<T> p, Supplier<S> supplier, CallbackInfoReturnable<Property.Value<T>> info)
	{
		if (!supplier.get().contains(p))
		{
			info.setReturnValue(null);
		}
	}
	
	@Inject(at = @At("HEAD"), cancellable = true, method = "method_30038", require = 0)
	private static <S extends State<?, S>, T extends Comparable<T>> void int_onXmapTo(Property<T> p, Pair<S, Property.Value<T>> pair, CallbackInfoReturnable<S> info)
	{
		if (pair.getSecond() == null)
		{
			info.setReturnValue(pair.getFirst());
		}
	}
	
	@Inject(at = @At("HEAD"), cancellable = true, method = "method_30037", require = 0)
	private static <S extends State<?, S>, T extends Comparable<T>> void int_onXmapFrom(Property<T> p, S s, CallbackInfoReturnable<Pair<S, Property.Value<T>>> info)
	{
		if (!s.contains(p))
		{
			info.setReturnValue(Pair.of(s, null));
		}
	}
	
	@Inject(at = @At("HEAD"), cancellable = true, method = "func_241486_a_", require = 0)
	private static <S extends State<?, S>, T extends Comparable<T>> void srg_onSetPartial(Property<T> p, Supplier<S> supplier, CallbackInfoReturnable<Property.Value<T>> info)
	{
		if (!supplier.get().contains(p))
		{
			info.setReturnValue(null);
		}
	}
	
	@Inject(at = @At("HEAD"), cancellable = true, method = "func_241485_a_", require = 0)
	private static <S extends State<?, S>, T extends Comparable<T>> void srg_onXmapTo(Property<T> p, Pair<S, Property.Value<T>> pair, CallbackInfoReturnable<S> info)
	{
		if (pair.getSecond() == null)
		{
			info.setReturnValue(pair.getFirst());
		}
	}
	
	@Inject(at = @At("HEAD"), cancellable = true, method = "func_241484_a_", require = 0)
	private static <S extends State<?, S>, T extends Comparable<T>> void srg_onXmapFrom(Property<T> p, S s, CallbackInfoReturnable<Pair<S, Property.Value<T>>> info)
	{
		if (!s.contains(p))
		{
			info.setReturnValue(Pair.of(s, null));
		}
	}
}
