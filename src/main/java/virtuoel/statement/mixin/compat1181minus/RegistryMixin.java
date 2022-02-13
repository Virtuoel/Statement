package virtuoel.statement.mixin.compat1181minus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.registry.Registry;
import virtuoel.statement.util.StatementRegistryExtensions;

@Mixin(Registry.class)
public abstract class RegistryMixin<T> implements StatementRegistryExtensions<T>
{
	@Shadow
	abstract int method_10249(T entry);
	
	@Override
	public int statement_getRawId(T entry)
	{
		return method_10249(entry);
	}
}
