package virtuoel.statement.mixin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import virtuoel.statement.api.StatementApi;

@Mixin(StateManager.Builder.class)
public abstract class StateManagerBuilderMixin
{
	@Unique private static final Logger LOGGER = LogManager.getLogger(StatementApi.MOD_ID);
	
	@Shadow abstract <T extends Comparable<T>> void validate(Property<T> property);
	
	@Redirect(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/state/StateManager$Builder;validate(Lnet/minecraft/state/property/Property;)V"))
	private void addValidateProxy(StateManager.Builder<?, ?> obj, Property<?> property)
	{
		try
		{
			validate(property);
		}
		catch(IllegalArgumentException e)
		{
			LOGGER.warn(e.getMessage());
			LOGGER.catching(e);
		}
	}
	
	@ModifyConstant(method = "validate", constant = @Constant(stringValue = " with <= 1 possible values"))
	private String validateExceptionMessageProxy(String orig)
	{
		return " with no possible values";
	}
	
	@ModifyConstant(method = "validate", constant = @Constant(ordinal = 0, intValue = 1))
	private int validateSizeProxy(int orig)
	{
		return 0;
	}
}
