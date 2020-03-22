package virtuoel.statement.mixin;

import java.util.IdentityHashMap;
import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.collection.IdList;
import virtuoel.statement.api.ClearableIdList;

@Mixin(IdList.class)
public abstract class IdListMixin implements ClearableIdList
{
	@Shadow private int nextId;
	@Shadow @Final private IdentityHashMap<Object, Integer> idMap;
	@Shadow @Final private List<Object> list;
	
	@Override
	public void statement_clear()
	{
		nextId = 0;
		idMap.clear();
		list.clear();
	}
}
