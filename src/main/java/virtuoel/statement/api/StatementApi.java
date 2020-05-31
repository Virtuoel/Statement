package virtuoel.statement.api;

import java.util.Collection;
import java.util.OptionalInt;

import javax.annotation.Nullable;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.IdList;

public interface StatementApi
{
	public static final String MOD_ID = "statement";
	
	public static final Collection<StatementApi> ENTRYPOINTS = FabricLoader.getInstance().getEntrypoints(MOD_ID, StatementApi.class);
	
	default <S> boolean shouldDeferState(IdList<S> idList, S state)
	{
		return false;
	}
	
	default <S> OptionalInt getSyncedId(IdList<S> idList, int id)
	{
		return getSyncedId(idList, idList.get(id));
	}
	
	default <S> OptionalInt getSyncedId(IdList<S> idList, @Nullable S state)
	{
		return OptionalInt.empty();
	}
}
