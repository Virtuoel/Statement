package virtuoel.statement.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.OptionalInt;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.collection.IdList;

public interface StatementApi
{
	public static final String MOD_ID = "statement";
	
	public static final Collection<StatementApi> ENTRYPOINTS = new ArrayList<>();
	
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
