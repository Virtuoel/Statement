package virtuoel.statement.mixin.sync.compat118plus.compat1201minus;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.world.chunk.ArrayPalette;
import virtuoel.statement.Statement;

@Mixin(ArrayPalette.class)
public class ArrayPaletteMixin<T>
{
	@Shadow @Final @Mutable
	IndexedIterable<T> idList;
	
	@ModifyArg(method = "getPacketSize()I", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/class_2540;method_10815(I)I", remap = false))
	private int getPacketSizeGetVarIntSizeBytesModify(int id)
	{
		return Statement.getSyncedStateId(idList, id, IndexedIterable::getRawId, IndexedIterable::get, IndexedIterable::size).orElse(id);
	}
}
