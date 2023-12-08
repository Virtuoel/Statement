package virtuoel.statement.mixin.compat118plus.compat1202minus;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.util.collection.IdList;
import virtuoel.statement.api.ClearableIdList;

@Mixin(IdList.class)
public abstract class IdListMixin<T> implements ClearableIdList
{
	@Shadow private int nextId;
	@Shadow @Final private Object2IntMap<T> idMap;
	@Shadow @Final private List<T> list;
	
	@Override
	public void statement_clear()
	{
		nextId = 0;
		idMap.clear();
		list.clear();
	}
}
