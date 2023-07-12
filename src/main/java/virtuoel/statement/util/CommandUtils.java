package virtuoel.statement.util;

import java.util.function.Supplier;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class CommandUtils
{
	public static void sendFeedback(ServerCommandSource source, Supplier<Text> text, boolean broadcastToOps)
	{
		source.sendFeedback(text, broadcastToOps);
	}
}
