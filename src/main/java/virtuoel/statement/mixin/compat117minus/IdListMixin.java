package virtuoel.statement.mixin.compat117minus;

import java.util.IdentityHashMap;
import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.collection.IdList;
import virtuoel.statement.api.ClearableIdList;

@Mixin(IdList.class)
public abstract class IdListMixin<T> implements ClearableIdList
{
	@Shadow private int nextId;
	@Shadow @Final private IdentityHashMap<T, Integer> idMap;
	@Shadow @Final private List<T> list;
	
	@Override
	public void statement_clear()
	{
		nextId = 0;
		idMap.clear();
		list.clear();
	}
}
