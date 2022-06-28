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

import net.minecraft.state.property.Property;
import virtuoel.statement.Statement;
import virtuoel.statement.util.HydrogenCompatibility;
import virtuoel.statement.util.StatementStateExtensions;

@Mixin(targets = "net.minecraft.class_2679", remap = false)
public abstract class AbstractStateMixin<O, S> implements StatementStateExtensions<S>
{
	@Shadow(remap = false) @Final @Mutable protected O field_12287;
	@Shadow(remap = false) @Final @Mutable private ImmutableMap<Property<?>, Comparable<?>> field_12285;
	@Shadow(remap = false) private Table<Property<?>, Comparable<?>, S> field_12288;
	
	@Unique String getMissingOwner = "";
	
	@Inject(method = "method_11654", cancellable = true, at = @At(value = "INVOKE", target = "Ljava/lang/IllegalArgumentException;<init>(Ljava/lang/String;)V"), remap = false)
	private <T extends Comparable<T>> void onGet(Property<T> property, CallbackInfoReturnable<T> info)
	{
		final String ownerString = this.field_12287.toString();
		
		if (!getMissingOwner.equals(ownerString))
		{
			Statement.LOGGER.info("Cannot get property {} as it does not exist in {}", property, this.field_12287);
			getMissingOwner = ownerString;
		}
		
		info.setReturnValue(cachedFallbacks.containsKey(property) ? property.getType().cast(cachedFallbacks.get(property)) : property.getValues().iterator().next());
	}
	
	@Unique String withMissingOwner = "";
	@Unique String withDisallowedOwner = "";
	
	@Inject(method = "method_11657", cancellable = true, at = @At(value = "INVOKE", target = "Ljava/lang/IllegalArgumentException;<init>(Ljava/lang/String;)V"), remap = false)
	private <T extends Comparable<T>, V extends T> void onWith(Property<T> property, V value, CallbackInfoReturnable<Object> info)
	{
		final String ownerString = this.field_12287.toString();
		
		if (this.field_12285.get(property) == null)
		{
			if (!withMissingOwner.equals(ownerString))
			{
				Statement.LOGGER.info("Cannot set property {} as it does not exist in {}", property, this.field_12287);
				withMissingOwner = ownerString;
			}
		}
		else if (!withDisallowedOwner.equals(ownerString))
		{
			Statement.LOGGER.info("Cannot set property {} to {} on {}, it is not an allowed value", property, value, this.field_12287);
			withDisallowedOwner = ownerString;
		}
		
		info.setReturnValue(this);
	}
	
	@Inject(at = @At("HEAD"), method = "method_11571", remap = false)
	private void onCreateWithTable(Map<Map<Property<?>, Comparable<?>>, S> map, CallbackInfo info)
	{
		field_12288 = null;
	}
	
	@Shadow(remap = false)
	abstract void method_11571(Map<Map<Property<?>, Comparable<?>>, ?> map);
	
	@Override
	public void statement_createWithTable(Map<Map<Property<?>, Comparable<?>>, ?> states)
	{
		method_11571(states);
	}
	
	@Shadow(remap = false)
	abstract ImmutableMap<Property<?>, Comparable<?>> method_11656();
	
	@Override
	public ImmutableMap<Property<?>, Comparable<?>> statement_getEntries()
	{
		return method_11656();
	}
	
	@Shadow
	abstract <T extends Comparable<T>, V extends T> S method_11657(Property<T> property, V value);
	
	@Override
	public <T extends Comparable<T>, V extends T> S statement_with(Property<T> property, V value)
	{
		return method_11657(property, value);
	}
	
	@Override
	public <V extends Comparable<V>> boolean statement_addEntry(final Property<V> property, final V value)
	{
		if (!field_12285.containsKey(property))
		{
			statement_setEntries(ImmutableMap.<Property<?>, Comparable<?>>builder().putAll(field_12285).put(property, value).build());
			
			return true;
		}
		
		return false;
	}
	
	@Unique final Map<Property<?>, Comparable<?>> cachedFallbacks = new HashMap<>();
	
	@Override
	public <V extends Comparable<V>> boolean statement_removeEntry(Property<V> property)
	{
		if (field_12285.containsKey(property))
		{
			final ImmutableMap.Builder<Property<?>, Comparable<?>> builder = ImmutableMap.builder();
			
			for (final Entry<Property<?>, Comparable<?>> entry : field_12285.entrySet())
			{
				final Property<?> key = entry.getKey();
				
				if (key != property)
				{
					builder.put(key, entry.getValue());
				}
			}
			
			cachedFallbacks.put(property, field_12285.get(property));
			
			statement_setEntries(builder.build());
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void statement_setEntries(ImmutableMap<Property<?>, Comparable<?>> entries)
	{
		this.field_12285 = HydrogenCompatibility.INSTANCE.wrapEntries(entries);
	}
}
