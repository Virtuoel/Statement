package virtuoel.statement.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import net.minecraft.util.collection.IdList;

public class PolymerCompatibility
{
	private static final boolean POLYMER_CORE_LOADED = ModLoaderUtils.isModLoaded("polymer-core");
	
	private static final Optional<Class<?>> POLYMER_ID_LIST_CLASS = POLYMER_CORE_LOADED ? ReflectionUtils.getClass("eu.pb4.polymer.core.impl.interfaces.PolymerIdList") : Optional.empty();
	private static final Optional<Method> SET_IGNORE_CALLS = POLYMER_CORE_LOADED ? ReflectionUtils.getMethod(POLYMER_ID_LIST_CLASS, "polymer$setIgnoreCalls", boolean.class) : Optional.empty();
	private static final Optional<Method> CLEAR = POLYMER_CORE_LOADED ? ReflectionUtils.getMethod(POLYMER_ID_LIST_CLASS, "polymer$clear") : Optional.empty();
	
	public static void preRecalculation(final IdList<?> idList)
	{
		if (POLYMER_CORE_LOADED)
		{
			SET_IGNORE_CALLS.ifPresent(m -> {
				try
				{
					m.invoke(idList, true);
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
				{
					
				}
			});
			CLEAR.ifPresent(m -> {
				try
				{
					m.invoke(idList);
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
				{
					
				}
			});
		}
	}
	
	public static void postRecalculation(final IdList<?> idList)
	{
		if (POLYMER_CORE_LOADED)
		{
			SET_IGNORE_CALLS.ifPresent(m -> {
				try
				{
					m.invoke(idList, false);
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
				{
					
				}
			});
		}
	}
}
