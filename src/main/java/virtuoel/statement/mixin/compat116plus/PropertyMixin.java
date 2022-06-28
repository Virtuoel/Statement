package virtuoel.statement.mixin.compat116plus;

import java.util.Collection;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.state.property.Property;
import virtuoel.statement.util.StatementPropertyExtensions;

@Mixin(Property.class)
public abstract class PropertyMixin<T extends Comparable<T>> implements StatementPropertyExtensions<T>
{
	@Shadow
	abstract String getName();
	@Shadow
	abstract Collection<T> getValues();
	@Shadow
	abstract String name(T value);
	@Shadow
	abstract Optional<T> parse(String name);
	
	@Override
	public String statement_getName()
	{
		return getName();
	}
	
	@Override
	public Collection<T> statement_getValues()
	{
		return getValues();
	}
	
	@Override
	public String statement_name(T value)
	{
		return name(value);
	}
	
	@Override
	public Optional<T> statement_parse(String name)
	{
		return parse(name);
	}
}
