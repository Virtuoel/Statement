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
	@Shadow(remap = false) private int field_11099;
	@Shadow(remap = false) @Final private Object2IntMap<T> field_11100;
	@Shadow(remap = false) @Final private List<T> field_11098;

	@Override
	public void statement_clear()
	{
		field_11099 = 0;
		field_11100.clear();
		field_11098.clear();
	}
}
