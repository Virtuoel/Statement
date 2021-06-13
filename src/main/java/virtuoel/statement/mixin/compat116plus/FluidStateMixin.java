package virtuoel.statement.mixin.compat116plus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import virtuoel.statement.util.StatementFluidStateExtensions;

@Mixin(FluidState.class)
public abstract class FluidStateMixin implements StatementFluidStateExtensions
{
	@Shadow
	abstract Fluid getFluid();
	
	@Override
	public Fluid statement_getFluid()
	{
		return getFluid();
	}
}
