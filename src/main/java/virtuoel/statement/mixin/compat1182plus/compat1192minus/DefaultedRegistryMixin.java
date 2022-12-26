package virtuoel.statement.mixin.compat1182plus.compat1192minus;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.serialization.Lifecycle;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import virtuoel.statement.util.RegistryKeyExtensions;

@Mixin(DefaultedRegistry.class)
public abstract class DefaultedRegistryMixin<T>
{
	@Shadow @Final Identifier defaultId;
	
	@Inject(method = "set(ILnet/minecraft/util/registry/RegistryKey;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/util/registry/RegistryEntry;", at = @At(value = "HEAD"))
	private void setDefault(int rawId, RegistryKey<T> registryKey, T entry, Lifecycle lifecycle, CallbackInfoReturnable<RegistryEntry<T>> info)
	{
		if (defaultId.equals(registryKey.getValue()))
		{
			((RegistryKeyExtensions) registryKey).statement_setValue(defaultId);
		}
	}
}
