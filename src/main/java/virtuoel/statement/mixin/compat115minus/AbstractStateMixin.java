package virtuoel.statement.mixin.compat115minus;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;

import net.minecraft.state.AbstractState;
import net.minecraft.state.property.Property;
import virtuoel.statement.Statement;
import virtuoel.statement.util.StatementStateExtensions;

@Mixin(AbstractState.class)
public abstract class AbstractStateMixin<O, S> implements StatementStateExtensions
{
	@Shadow @Final @Mutable protected O owner;
	@Shadow @Final @Mutable private ImmutableMap<Property<?>, Comparable<?>> entries;
	@Shadow private Table<Property<?>, Comparable<?>, S> withTable;
	
	@Unique boolean loggedGetMissing = false;
	
	@Inject(at = @At("HEAD"), method = "get", cancellable = true)
	private <T extends Comparable<T>> void onGet(Property<T> property, CallbackInfoReturnable<T> info)
	{
		final Comparable<?> currentValue = this.entries.get(property);
		
		if (currentValue == null)
		{
			if (!loggedGetMissing)
			{
				Statement.LOGGER.info("Cannot get property {} as it does not exist in {}", property, this.owner);
				loggedGetMissing = true;
			}
			
			info.setReturnValue(cachedFallbacks.containsKey(property) ? property.getType().cast(cachedFallbacks.get(property)) : property.getValues().iterator().next());
		}
	}
	
	@Unique boolean loggedWithMissing = false;
	@Unique boolean loggedWithDisallowed = false;
	
	@Inject(at = @At("HEAD"), method = "with", cancellable = true)
	private <T extends Comparable<T>, V extends T> void onWith(Property<T> property, V value, CallbackInfoReturnable<Object> info)
	{
		final Comparable<?> currentValue = this.entries.get(property);
		
		if (currentValue == null)
		{
			if (!loggedWithMissing)
			{
				Statement.LOGGER.info("Cannot set property {} as it does not exist in {}", property, this.owner);
				loggedWithMissing = true;
			}
			
			info.setReturnValue(this);
		}
		else if (currentValue != value && withTable.get(property, value) == null)
		{
			if (!loggedWithDisallowed)
			{
				Statement.LOGGER.info("Cannot set property {} to {} on {}, it is not an allowed value", property, value, this.owner);
				loggedWithDisallowed = true;
			}
			
			info.setReturnValue(this);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "createWithTable")
	private void onCreateWithTable(Map<Map<Property<?>, Comparable<?>>, S> map, CallbackInfo info)
	{
		withTable = null;
	}
	
	@Shadow
	abstract void createWithTable(Map<Map<Property<?>, Comparable<?>>, ?> map);
	
	@Override
	public void statement_createWithTable(Map<Map<Property<?>, Comparable<?>>, ?> states)
	{
		createWithTable(states);
	}
	
	@Override
	public <V extends Comparable<V>> boolean statement_addEntry(final Property<V> property, final V value)
	{
		if (!entries.containsKey(property))
		{
			entries = ImmutableMap.<Property<?>, Comparable<?>>builder().putAll(entries).put(property, value).build();
			
			return true;
		}
		
		return false;
	}
	
	@Unique final Map<Property<?>, Comparable<?>> cachedFallbacks = new HashMap<>();
	
	@Override
	public <V extends Comparable<V>> boolean statement_removeEntry(Property<V> property)
	{
		if (entries.containsKey(property))
		{
			final ImmutableMap.Builder<Property<?>, Comparable<?>> builder = ImmutableMap.builder();
			
			for (final Entry<Property<?>, Comparable<?>> entry : entries.entrySet())
			{
				final Property<?> key = entry.getKey();
				
				if (key != property)
				{
					builder.put(key, entry.getValue());
				}
			}
			
			cachedFallbacks.put(property, entries.get(property));
			
			entries = builder.build();
			
			return true;
		}
		
		return false;
	}
}
