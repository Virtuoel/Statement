package virtuoel.statement.api;

import java.util.Collection;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.state.PropertyContainer;

public interface StatementApi
{
	public static final String MOD_ID = "statement";
	
	public static final Collection<StatementApi> ENTRYPOINTS = FabricLoader.getInstance().getEntrypoints(MOD_ID, StatementApi.class);
	
	default boolean shouldDeferState(PropertyContainer<?> state)
	{
		return false;
	}
}
