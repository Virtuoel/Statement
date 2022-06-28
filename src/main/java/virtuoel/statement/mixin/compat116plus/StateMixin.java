package virtuoel.statement.mixin.compat116plus;

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
import com.mojang.serialization.MapCodec;

import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import virtuoel.statement.Statement;
import virtuoel.statement.util.HydrogenCompatibility;
import virtuoel.statement.util.StatementStateExtensions;

@Mixin(State.class)
public abstract class StateMixin<O, S> implements StatementStateExtensions<S>
{
	@Shadow @Final @Mutable protected O owner;
	@Shadow @Final @Mutable private ImmutableMap<Property<?>, Comparable<?>> entries;
	@Shadow private Table<Property<?>, Comparable<?>, S> withTable;
	@Shadow @Final @Mutable MapCodec<S> codec;
	
	@Unique String getMissingOwner = "";
	
	@Inject(method = "get", cancellable = true, at = @At(value = "INVOKE", target = "Ljava/lang/IllegalArgumentException;<init>(Ljava/lang/String;)V"))
	private <T extends Comparable<T>> void onGet(Property<T> property, CallbackInfoReturnable<T> info)
	{
		final String ownerString = this.owner.toString();
		
		if (!getMissingOwner.equals(ownerString))
		{
			Statement.LOGGER.info("Cannot get property {} as it does not exist in {}", property, this.owner);
			getMissingOwner = ownerString;
		}
		
		info.setReturnValue(cachedFallbacks.containsKey(property) ? property.getType().cast(cachedFallbacks.get(property)) : property.getValues().iterator().next());
	}
	
	@Unique String withMissingOwner = "";
	@Unique String withDisallowedOwner = "";
	
	@Inject(method = "with", cancellable = true, at = @At(value = "INVOKE", target = "Ljava/lang/IllegalArgumentException;<init>(Ljava/lang/String;)V"))
	private <T extends Comparable<T>, V extends T> void onWith(Property<T> property, V value, CallbackInfoReturnable<Object> info)
	{
		final String ownerString = this.owner.toString();
		
		if (this.entries.get(property) == null)
		{
			if (!withMissingOwner.equals(ownerString))
			{
				Statement.LOGGER.info("Cannot set property {} as it does not exist in {}", property, this.owner);
				withMissingOwner = ownerString;
			}
		}
		else if (!withDisallowedOwner.equals(ownerString))
		{
			Statement.LOGGER.info("Cannot set property {} to {} on {}, it is not an allowed value", property, value, this.owner);
			withDisallowedOwner = ownerString;
		}
		
		info.setReturnValue(this);
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
	
	@Shadow
	abstract ImmutableMap<Property<?>, Comparable<?>> getEntries();
	
	@Override
	public ImmutableMap<Property<?>, Comparable<?>> statement_getEntries()
	{
		return getEntries();
	}
	
	@Shadow
	abstract <T extends Comparable<T>, V extends T> S with(Property<T> property, V value);
	
	@Override
	public <T extends Comparable<T>, V extends T> S statement_with(Property<T> property, V value)
	{
		return with(property, value);
	}
	
	@Override
	public <V extends Comparable<V>> boolean statement_addEntry(final Property<V> property, final V value)
	{
		if (!entries.containsKey(property))
		{
			statement_setEntries(ImmutableMap.<Property<?>, Comparable<?>>builder().putAll(entries).put(property, value).build());
			
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
			
			statement_setEntries(builder.build());
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void statement_setEntries(ImmutableMap<Property<?>, Comparable<?>> entries)
	{
		this.entries = HydrogenCompatibility.INSTANCE.wrapEntries(entries);
	}
	
	@Override
	public Object statement_getCodec()
	{
		return this.codec;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void statement_setCodec(Object codec)
	{
		this.codec = (MapCodec<S>) codec;
	}
}
