package virtuoel.statement.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.util.IdList;
import net.minecraft.world.chunk.BiMapPalette;
import virtuoel.statement.api.StatementApi;

@Mixin(BiMapPalette.class)
public class BiMapPaletteMixin<T>
{
	@Redirect(method = "toPacket(Lnet/minecraft/util/PacketByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/IdList;getId(Ljava/lang/Object;)I"))
	private int toPacketGetIdProxy(IdList<T> idList, T state)
	{
		for (final StatementApi api : StatementApi.ENTRYPOINTS)
		{
			final Optional<Integer> syncedId = api.getSyncedId(idList, state);
			
			if (syncedId.isPresent())
			{
				return syncedId.get();
			}
		}
		
		return idList.getId(state);
	}
	
	@Redirect(method = "getPacketSize()I", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/IdList;getId(Ljava/lang/Object;)I"))
	private int getPacketSizeGetIdProxy(IdList<T> idList, T state)
	{
		for (final StatementApi api : StatementApi.ENTRYPOINTS)
		{
			final Optional<Integer> syncedId = api.getSyncedId(idList, state);
			
			if (syncedId.isPresent())
			{
				return syncedId.get();
			}
		}
		
		return idList.getId(state);
	}
}
