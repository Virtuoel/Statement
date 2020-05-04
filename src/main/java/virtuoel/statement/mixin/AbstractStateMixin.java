package virtuoel.statement.mixin;

import java.util.LinkedList;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;

import net.minecraft.state.AbstractState;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.util.collection.MapUtil;
import virtuoel.statement.util.StatementStateExtensions;

@Mixin(AbstractState.class)
public abstract class AbstractStateMixin<O, S> implements State<S>, StatementStateExtensions
{
	@Shadow @Final @Mutable protected O owner;
	@Shadow @Final @Mutable private ImmutableMap<Property<?>, Comparable<?>> entries;
	@Shadow private Table<Property<?>, Comparable<?>, S> withTable;
	
	@Inject(at = @At("HEAD"), method = "with", cancellable = true)
	private <T extends Comparable<T>, V extends T> void onWith(Property<T> property, V value, CallbackInfoReturnable<Object> info)
	{
		final Comparable<?> currentValue = this.entries.get(property);
		if (currentValue == null)
		{
			LOGGER.info("Cannot set property {} as it does not exist in {}", property, this.owner);
			info.setReturnValue(this);
		}
		else if (currentValue != value && withTable.get(property, value) == null)
		{
			LOGGER.info("Cannot set property {} to {} on {}, it is not an allowed value", property, value, this.owner);
			info.setReturnValue(this);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "createWithTable")
	private void onCreateWithTable(Map<Map<Property<?>, Comparable<?>>, S> map, CallbackInfo info)
	{
		withTable = null;
	}
	
	@Override
	public <V extends Comparable<V>> boolean statement_addEntry(final Property<V> property, final V value)
	{
		if (!entries.containsKey(property))
		{
			final LinkedList<Property<?>> keys = new LinkedList<>(entries.keySet());
			keys.add(property);
			
			final LinkedList<Comparable<?>> values = new LinkedList<>(entries.values());
			values.add(value);
			
			entries = ImmutableMap.copyOf(MapUtil.createMap(keys, values));
			
			return true;
		}
		
		return false;
	}
}
