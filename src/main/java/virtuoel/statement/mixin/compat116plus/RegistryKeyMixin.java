package virtuoel.statement.mixin.compat116plus;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import virtuoel.statement.util.RegistryKeyExtensions;

@Mixin(RegistryKey.class)
public class RegistryKeyMixin implements RegistryKeyExtensions
{
	@Shadow @Final @Mutable
	Identifier value;
	
	@Override
	public void statement_setValue(Identifier value)
	{
		this.value = value;
	}
}
