package virtuoel.statement.api;

import java.util.Collection;
import java.util.OptionalInt;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.collection.IdList;

public interface StatementApi
{
	public static final String MOD_ID = "statement";
	
	public static final Collection<StatementApi> ENTRYPOINTS = FabricLoader.getInstance().getEntrypoints(MOD_ID, StatementApi.class);
	
	default <S> boolean shouldDeferState(IdList<S> idList, S state)
	{
		return false;
	}
	
	@Deprecated
	default <S> OptionalInt getSyncedId(IdList<S> idList, int id)
	{
		return getSyncedId(idList, id, IdList::getRawId, IdList::get, IdList::size);
	}
	
	@Deprecated
	default <S> OptionalInt getSyncedId(IdList<S> idList, @Nullable S state)
	{
		return getSyncedId(idList, state, IdList::getRawId, IdList::get, IdList::size);
	}
	
	default <S, L extends Iterable<S>> OptionalInt getSyncedId(L idList, int id, BiFunction<L, S, Integer> idFunc, BiFunction<L, Integer, S> getFunc, Function<L, Integer> sizeFunc)
	{
		return getSyncedId(idList, getFunc.apply(idList, id), idFunc, getFunc, sizeFunc);
	}
	
	default <S, L extends Iterable<S>> OptionalInt getSyncedId(L idList, @Nullable S state, BiFunction<L, S, Integer> idFunc, BiFunction<L, Integer, S> getFunc, Function<L, Integer> sizeFunc)
	{
		return OptionalInt.empty();
	}
}
