package virtuoel.statement.api;

import javax.annotation.Nullable;

import net.minecraft.state.property.Property;

public interface MutableStateManager
{
	@Nullable
	default Property<?> statement_addProperty(final Property<?> property)
	{
		return null;
	}
	
	@Nullable
	default Property<?> statement_removeProperty(final String propertyName)
	{
		return null;
	}
	
	default boolean statement_removeProperty(final Property<?> property)
	{
		return false;
	}
}
