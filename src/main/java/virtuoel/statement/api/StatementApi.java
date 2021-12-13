package virtuoel.statement.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.OptionalInt;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.ApiStatus;
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
	
	@Deprecated @ApiStatus.ScheduledForRemoval(inVersion = "5.0.0")
	default <S> OptionalInt getSyncedId(IdList<S> idList, int id)
	{
		return getSyncedId(idList, idList.get(id));
	}
	
	@Deprecated @ApiStatus.ScheduledForRemoval(inVersion = "5.0.0")
	default <S> OptionalInt getSyncedId(IdList<S> idList, @Nullable S state)
	{
		return OptionalInt.empty();
	}
	
	default <S, L extends Iterable<S>> OptionalInt getSyncedId(L idList, int id, BiFunction<L, S, Integer> idFunc, BiFunction<L, Integer, S> getFunc, Function<L, Integer> sizeFunc)
	{
		if (idList instanceof IdList)
		{
			@SuppressWarnings("unchecked")
			final OptionalInt ret = getSyncedId((IdList<S>) idList, id);
			
			if (ret.isPresent())
			{
				return ret;
			}
		}
		
		return getSyncedId(idList, getFunc.apply(idList, id), idFunc, getFunc, sizeFunc);
	}
	
	default <S, L extends Iterable<S>> OptionalInt getSyncedId(L idList, @Nullable S state, BiFunction<L, S, Integer> idFunc, BiFunction<L, Integer, S> getFunc, Function<L, Integer> sizeFunc)
	{
		if (idList instanceof IdList)
		{
			@SuppressWarnings("unchecked")
			final OptionalInt ret = getSyncedId((IdList<S>) idList, state);
			
			if (ret.isPresent())
			{
				return ret;
			}
		}
		
		return OptionalInt.empty();
	}
}
