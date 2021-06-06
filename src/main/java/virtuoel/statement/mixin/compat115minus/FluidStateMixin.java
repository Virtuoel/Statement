package virtuoel.statement.mixin.compat115minus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.fluid.Fluid;
import virtuoel.statement.util.StatementFluidStateExtensions;

@Mixin(targets = "net.minecraft.class_3610", remap = false)
public interface FluidStateMixin extends StatementFluidStateExtensions
{
	@Shadow(remap = false)
	Fluid method_15772();
	
	@Override
	default Fluid statement_getFluid()
	{
		return method_15772();
	}
}
