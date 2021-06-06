package virtuoel.statement.mixin.compat115minus;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.state.State;
import virtuoel.statement.util.StatementStateExtensions;

@Mixin(State.class)
public interface StateMixin<O, S> extends StatementStateExtensions<S>
{
	
}
