package virtuoel.statement.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;

import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import virtuoel.statement.api.RefreshableStateManager;
import virtuoel.statement.util.StatementStateExtensions;

@Mixin(StateManager.class)
public class StateManagerMixin<O, S extends State<O, S>> implements RefreshableStateManager<O, S>
{
	@Shadow @Final @Mutable ImmutableSortedMap<String, Property<?>> properties;
	@Shadow @Final @Mutable ImmutableList<S> states;
	
	@Override
	public void statement_setStateList(ImmutableList<S> states)
	{
		this.states = states;
	}
	
	@Override
	public Map<String, Property<?>> statement_getProperties()
	{
		return properties;
	}
	
	@Override
	public void statement_setProperties(Map<String, Property<?>> properties)
	{
		if (properties instanceof ImmutableSortedMap<?, ?>)
		{
			this.properties = (ImmutableSortedMap<String, Property<?>>) properties;
		}
	}
	
	@Override
	public <V extends Comparable<V>> Property<?> statement_addProperty(Property<V> property, V defaultValue)
	{
		final Property<?> ret = RefreshableStateManager.super.statement_addProperty(property, defaultValue);
		
		if (ret == null)
		{
			for (final State<O, S> state : states)
			{
				StatementStateExtensions.statement_cast(state).statement_addEntry(property, defaultValue);
			}
		}
		
		return ret;
	}
	
	@Override
	public Property<?> statement_removeProperty(String propertyName)
	{
		final Property<?> ret =  RefreshableStateManager.super.statement_removeProperty(propertyName);
		
		if (ret != null)
		{
			for (final State<O, S> state : states)
			{
				StatementStateExtensions.statement_cast(state).statement_removeEntry(ret);
			}
		}
		
		return ret;
	}
}
