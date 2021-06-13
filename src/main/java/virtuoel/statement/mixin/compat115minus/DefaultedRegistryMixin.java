package virtuoel.statement.mixin.compat115minus;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;

@Mixin(DefaultedRegistry.class)
public abstract class DefaultedRegistryMixin
{
	@Shadow @Final Identifier defaultId;
	
	@ModifyArg(method = "method_10273(ILnet/minecraft/class_2960;Ljava/lang/Object;)Ljava/lang/Object;", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2370;method_10273(ILnet/minecraft/class_2960;Ljava/lang/Object;)Ljava/lang/Object;", remap = false), remap = false)
	private Identifier setDefault(Identifier id)
	{
		return defaultId.equals(id) ? defaultId : id;
	}
}
