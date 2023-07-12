package virtuoel.statement;

import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.api.ClientModInitializer;
import virtuoel.statement.util.FabricApiCompatibility;
import virtuoel.statement.util.ModLoaderUtils;

@ApiStatus.Internal
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
