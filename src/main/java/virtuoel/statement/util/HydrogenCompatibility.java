package virtuoel.statement.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

import net.minecraftforge.fml.ModList;

public class HydrogenCompatibility
{
	private static final boolean HYDROGEN_LOADED = ModList.get().isLoaded("hydrogen");
	
	public static final HydrogenCompatibility INSTANCE = new HydrogenCompatibility();
	
	private final Optional<Class<?>> classConstructorsClass;
	
	private final Optional<Method> createFastImmutableMap;
	
	private boolean enabled;
	
	public HydrogenCompatibility()
	{
		this.enabled = HYDROGEN_LOADED;
		
		if (this.enabled)
		{
			this.classConstructorsClass = getClass("me.jellysquid.mods.hydrogen.common.jvm.ClassConstructors");
			
			this.createFastImmutableMap = getMethod(classConstructorsClass, "createFastImmutableMap");
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
	
	private static Optional<Method> getMethod(final Optional<Class<?>> classObj, final String methodName, Class<?>... args)
	{
		return classObj.map(c ->
		{
			try
			{
				final Method m = c.getMethod(methodName, args);
				m.setAccessible(true);
				return m;
			}
			catch (SecurityException | NoSuchMethodException e)
			{
				
			}
			return null;
		});
	}
	
	private static Optional<Class<?>> getClass(final String className)
	{
		try
		{
			return Optional.of(Class.forName(className));
		}
		catch (ClassNotFoundException e)
		{
			
		}
		return Optional.empty();
	}
}
