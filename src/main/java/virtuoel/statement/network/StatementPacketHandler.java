package virtuoel.statement.network;

import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import virtuoel.statement.Statement;

public class StatementPacketHandler
{
	private static final String PROTOCOL_VERSION = Integer.toString(1);
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
		Statement.id("main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals
	);
	
	public static void init()
	{
		int regId = 0;
		INSTANCE.registerMessage(regId++, StateValidationPacket.class, StateValidationPacket::encode, StateValidationPacket::new, StateValidationPacket::handle);
	}
}
