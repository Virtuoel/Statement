package virtuoel.statement.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

public final class ReflectionUtils
{
	public static Optional<Field> getField(final Optional<Class<?>> classObj, final String fieldName)
	{
		return classObj.map(c ->
		{
			try
			{
				final Field f = c.getDeclaredField(fieldName);
				f.setAccessible(true);
				return f;
			}
			catch (SecurityException | NoSuchFieldException e)
			{
				return null;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(final Optional<Class<?>> classObj, final String fieldName, final Object object, final T defaultValue)
	{
		return getField(classObj, fieldName).map(f ->
		{
			try
			{
				return (T) f.get(object);
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				return defaultValue;
			}
		}).orElse(null);
	}
	
	public static void setField(final Optional<Class<?>> classObj, final String fieldName, final Object object, final Object value)
	{
		getField(classObj, fieldName).ifPresent(f ->
		{
			try
			{
				f.set(object, value);
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				
			}
		});
	}
	
	public static Optional<Method> getMethod(final Optional<Class<?>> classObj, final String methodName, final Class<?>... args)
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
				return null;
			}
		});
	}
	
	public static <T> Optional<Constructor<T>> getConstructor(final Optional<Class<T>> clazz, final Class<?>... params)
	{
		return clazz.map(c ->
		{
			try
			{
				return c.getConstructor(params);
			}
			catch (NoSuchMethodException | SecurityException e)
			{
				return null;
			}
		});
	}
	
	public static Optional<Class<?>> getClass(final String className, final String... classNames)
	{
		Optional<Class<?>> ret = getClass(className);
		
		for (final String name : classNames)
		{
			if (ret.isPresent())
			{
				return ret;
			}
			
			ret = getClass(name);
		}
		
		return ret;
	}
	
	public static Optional<Class<?>> getClass(final String className)
	{
		try
		{
			return Optional.of(Class.forName(className));
		}
		catch (ClassNotFoundException e)
		{
			return Optional.empty();
		}
	}
	
	private ReflectionUtils()
	{
		
	}
}
