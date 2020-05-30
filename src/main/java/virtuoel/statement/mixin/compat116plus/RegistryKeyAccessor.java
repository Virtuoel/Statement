package virtuoel.statement.mixin.compat116plus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;

@Mixin(RegistryKey.class)
public interface RegistryKeyAccessor
{
	@Accessor
	void setValue(Identifier value);
}
