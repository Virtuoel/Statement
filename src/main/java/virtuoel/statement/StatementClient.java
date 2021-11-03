package virtuoel.statement;

import net.fabricmc.api.ClientModInitializer;
import virtuoel.statement.util.FabricApiCompatibility;
import virtuoel.statement.util.ModLoaderUtils;

public class StatementClient implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		final boolean fabricNetworkingLoaded = ModLoaderUtils.isModLoaded("fabric-networking-api-v1");
		
		if (fabricNetworkingLoaded)
		{
			FabricApiCompatibility.setupClientNetworking();
		}
	}
}
