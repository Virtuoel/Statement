package virtuoel.statement.mixin.sync;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import virtuoel.statement.Statement;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin
{
	@Shadow abstract BlockState getBlockState();
	
	@ModifyArg(method = "createSpawnPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;<init>(Lnet/minecraft/entity/Entity;I)V"))
	private int createSpawnPacketGetRawIdFromStateModify(int id)
	{
		return Statement.getSyncedBlockStateId(getBlockState()).orElse(id);
	}
}
