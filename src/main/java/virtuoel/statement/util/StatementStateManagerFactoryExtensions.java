package virtuoel.statement.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMap;

import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;

public interface StatementStateManagerFactoryExtensions<O, S extends State<O, S>>
{
	@Nullable
	static final Method CREATE_METHOD = getCreateMethod();
	
	@Nullable
	static Method getCreateMethod()
	{
		try
		{
			return StateManager.Factory.class.getMethod("create", Object.class, ImmutableMap.class);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	default S statement_create(O owner, ImmutableMap<Property<?>, Comparable<?>> entries)
	{
		if (CREATE_METHOD != null)
		{
			try
			{
				return (S) CREATE_METHOD.invoke(this, owner, entries);
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				
			}
		}
		
		return null;
	}
}
