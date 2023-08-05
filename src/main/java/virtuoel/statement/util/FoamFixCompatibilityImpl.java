package virtuoel.statement.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import virtuoel.statement.api.compatibility.FoamFixCompatibility;

public class FoamFixCompatibilityImpl implements FoamFixCompatibility
{
	private static final boolean FOAMFIX_LOADED = ModLoaderUtils.isModLoaded("foamfix");
	
	private final Optional<Class<?>> orderingClass;
	private final Optional<Class<?>> factoryClass;
	private final Optional<Class<?>> stateClass;
	private final Optional<Class<?>> valueMapperClass;
	
	private final Optional<Map<Property<?>, ?>> propertyEntryMap;
	
	private final Optional<Field> factoryMapper;
	private final Optional<Field> stateOwner;
	
	private boolean enabled;
	
	public FoamFixCompatibilityImpl()
	{
		this.enabled = FOAMFIX_LOADED;
		
		if (this.enabled)
		{
			this.orderingClass = ReflectionUtils.getClass("pl.asie.foamfix.state.PropertyOrdering");
			this.factoryClass = ReflectionUtils.getClass("pl.asie.foamfix.state.FoamyStateFactory$Factory");
			this.stateClass = ReflectionUtils.getClass("pl.asie.foamfix.state.FoamyBlockStateMapped");
			this.valueMapperClass = ReflectionUtils.getClass("pl.asie.foamfix.state.PropertyValueMapperImpl");
			
			this.propertyEntryMap = ReflectionUtils.getField(orderingClass, "entryMap").map(f ->
			{
				try
				{
					@SuppressWarnings("unchecked")
					final Map<Property<?>, ?> map = (Map<Property<?>, ?>) f.get(null);
					return map;
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					
				}
				return null;
			});
			
			this.factoryMapper = ReflectionUtils.getField(factoryClass, "mapper");
			this.stateOwner = ReflectionUtils.getField(stateClass, "owner");
		}
		else
		{
			this.orderingClass = Optional.empty();
			this.factoryClass = Optional.empty();
			this.stateClass = Optional.empty();
			this.valueMapperClass = Optional.empty();
			
			this.propertyEntryMap = Optional.empty();
			
			this.factoryMapper = Optional.empty();
			this.stateOwner = Optional.empty();
		}
	}
	
	@Override
	public void enable()
	{
		this.enabled = FOAMFIX_LOADED;
	}
	
	@Override
	public void disable()
	{
		this.enabled = false;
	}
	
	@Override
	public boolean isEnabled()
	{
		return this.enabled;
	}
	
	@Override
	public void removePropertyFromEntryMap(Property<?> property)
	{
		if (isEnabled())
		{
			propertyEntryMap.ifPresent(map ->
			{
				map.remove(property);
			});
		}
	}
	
	@Override
	public Optional<Object> constructPropertyValueMapper(Collection<Property<?>> properties)
	{
		if (isEnabled())
		{
			return valueMapperClass.map(c ->
			{
				return constructPropertyValueMapper(c, properties);
			});
		}
		return Optional.empty();
	}
	
	@Nullable
	private static Object constructPropertyValueMapper(Class<?> clazz, Collection<Property<?>> properties)
	{
		try
		{
			return clazz.getConstructor(Collection.class).newInstance(properties);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
		{
			return null;
		}
	}
	
	@Override
	public void setFactoryMapper(final Optional<?> factory, final Optional<?> mapper)
	{
		if (isEnabled())
		{
			mapper.ifPresent(m ->
			{
				factory.ifPresent(f ->
				{
					factoryClass.filter(c -> c.isInstance(f)).flatMap(c -> factoryMapper).ifPresent(field ->
					{
						try
						{
							field.set(f, m);
						}
						catch (IllegalArgumentException | IllegalAccessException e)
						{
							
						}
					});
				});
			});
		}
	}
	
	@Override
	public void setStateOwner(final State<?, ?> state, final Optional<?> owner)
	{
		if (isEnabled())
		{
			owner.ifPresent(o ->
			{
				stateClass.filter(c -> c.isInstance(state)).flatMap(c -> stateOwner).ifPresent(f ->
				{
					try
					{
						f.set(state, o);
					}
					catch (IllegalArgumentException | IllegalAccessException e)
					{
						
					}
				});
			});
		}
	}
}
