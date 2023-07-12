package virtuoel.statement.mixin.compat1182plus.compat1192minus;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.serialization.Lifecycle;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleDefaultedRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import virtuoel.statement.util.RegistryKeyExtensions;

@Mixin(SimpleDefaultedRegistry.class)
public abstract class DefaultedRegistryMixin<T>
{
	@Shadow @Final Identifier defaultId;
	
	@Inject(method = "method_10273", at = @At(value = "HEAD"), remap = false)
	private void setDefault(int rawId, RegistryKey<T> registryKey, T entry, Lifecycle lifecycle, CallbackInfoReturnable<RegistryEntry<T>> info)
	{
		if (defaultId.equals(registryKey.getValue()))
		{
			((RegistryKeyExtensions) registryKey).statement_setValue(defaultId);
		}
	}
}
