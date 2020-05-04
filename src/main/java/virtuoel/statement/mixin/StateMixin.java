package virtuoel.statement.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.state.State;
import virtuoel.statement.util.StatementStateExtensions;

@Mixin(State.class)
public interface StateMixin extends StatementStateExtensions
{
	
}
