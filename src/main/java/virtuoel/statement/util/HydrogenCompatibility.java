package virtuoel.statement.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

public class HydrogenCompatibility
{
	private static final boolean HYDROGEN_LOADED = ModLoaderUtils.isModLoaded("hydrogen");
	
	public static final HydrogenCompatibility INSTANCE = new HydrogenCompatibility();
	
	private final Optional<Class<?>> classConstructorsClass;
	
	private final Optional<Method> createFastImmutableMap;
	
	private boolean enabled;
	
	public HydrogenCompatibility()
	{
		this.enabled = HYDROGEN_LOADED;
		
		if (this.enabled)
		{
			this.classConstructorsClass = ReflectionUtils.getClass("me.jellysquid.mods.hydrogen.common.jvm.ClassConstructors");
			
			this.createFastImmutableMap = ReflectionUtils.getMethod(classConstructorsClass, "createFastImmutableMap");
		}
		else
		{
			this.classConstructorsClass = Optional.empty();
			
			this.createFastImmutableMap = Optional.empty();
		}
	}
	
	@SuppressWarnings("unchecked")
	public <K, V> ImmutableMap<K, V> wrapEntries(ImmutableMap<K, V> entries)
	{
		if (this.enabled)
		{
			return createFastImmutableMap.map(m ->
			{
				try
				{
					return (ImmutableMap<K, V>) m.invoke(null, entries);
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
				{
					return null;
				}
			}).orElse(entries);
		}
		
		return entries;
	}
}
