package virtuoel.statement.util;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;

public interface StatementFluidStateExtensions extends StatementStateExtensions<FluidState>
{
	Fluid statement_getFluid();
}
