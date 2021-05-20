package virtuoel.statement.mixin.compat116plus;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.serialization.Lifecycle;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.RegistryKey;
import virtuoel.statement.util.RegistryKeyExtensions;

@Mixin(DefaultedRegistry.class)
public abstract class DefaultedRegistryMixin<T>
{
	@Shadow @Final Identifier defaultId;
	
	@Inject(method = "set", at = @At(value = "HEAD"))
	private <V extends T> void setDefault(int rawId, RegistryKey<T> registryKey, V entry, Lifecycle lifecycle, CallbackInfoReturnable<V> info)
	{
		if (defaultId.equals(registryKey.getValue()))
		{
			((RegistryKeyExtensions) registryKey).statement_setValue(defaultId);
		}
	}
}
