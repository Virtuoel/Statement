package virtuoel.statement.mixin.compat115minus;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import virtuoel.statement.util.StatementStateManagerFactoryExtensions;

@Mixin(StateManager.Factory.class)
public interface StateManagerFactoryMixin<O, S extends State<S>> extends StatementStateManagerFactoryExtensions<O, S>
{
	
}
