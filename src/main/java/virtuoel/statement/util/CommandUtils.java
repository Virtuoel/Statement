package virtuoel.statement.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import virtuoel.statement.Statement;

public class CommandUtils
{
	public static final Method SEND_FEEDBACK;
	
	static
	{
		final MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
		final Int2ObjectMap<Method> h = new Int2ObjectArrayMap<Method>();
		
		String mapped = "unset";
		
		try
		{
			final boolean is119Minus = VersionUtils.MINOR <= 19;
			
			if (is119Minus)
			{
				mapped = mappingResolver.mapMethodName("intermediary", "net.minecraft.class_2168", "method_9226", "(Lnet/minecraft/class_2561;Z)V");
				h.put(0, ServerCommandSource.class.getMethod(mapped, Text.class, boolean.class));
			}
		}
		catch (NoSuchMethodException | SecurityException e1)
		{
			Statement.LOGGER.error("Last method lookup: {}", mapped);
			Statement.LOGGER.catching(e1);
		}
		
		SEND_FEEDBACK = h.get(0);
	}
	
	public static void sendFeedback(ServerCommandSource source, Supplier<Text> text, boolean broadcastToOps)
	{
		if (SEND_FEEDBACK != null)
		{
			try
			{
				SEND_FEEDBACK.invoke(source, text.get(), broadcastToOps);
				
				return;
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				Statement.LOGGER.catching(e);
			}
		}
		
		source.sendFeedback(text, broadcastToOps);
	}
}
