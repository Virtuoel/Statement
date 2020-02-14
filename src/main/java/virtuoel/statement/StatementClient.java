package virtuoel.statement;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import virtuoel.statement.util.FabricApiCompatibility;

public class StatementClient implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		final boolean fabricNetworkingLoaded = FabricLoader.getInstance().isModLoaded("fabric-networking-v0");
		
		if (fabricNetworkingLoaded)
		{
			FabricApiCompatibility.setupClientNetworking();
		}
	}
}
