package virtuoel.statement.mixin.sync;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.util.IdList;
import net.minecraft.world.chunk.ArrayPalette;
import virtuoel.statement.Statement;

@Mixin(ArrayPalette.class)
public class ArrayPaletteMixin<T>
{
	@Redirect(method = "toPacket(Lnet/minecraft/util/PacketByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/IdList;getId(Ljava/lang/Object;)I"))
	private int toPacketGetIdProxy(IdList<T> idList, T state)
	{
		return Statement.getSyncedStateId(idList, state).orElseGet(() -> idList.getId(state));
	}
	
	@Redirect(method = "getPacketSize()I", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/IdList;getId(Ljava/lang/Object;)I"))
	private int getPacketSizeGetIdProxy(IdList<T> idList, T state)
	{
		return Statement.getSyncedStateId(idList, state).orElseGet(() -> idList.getId(state));
	}
}
