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

import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import virtuoel.statement.api.StatementApi;

@Mixin(StateFactory.Builder.class)
public abstract class StateFactoryBuilderMixin
{
	@Unique private static final Logger LOGGER = LogManager.getLogger(StatementApi.MOD_ID);
	
	@Shadow abstract <T extends Comparable<T>> void validate(Property<T> property_1);
	
	@Redirect(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/state/StateFactory$Builder;validate(Lnet/minecraft/state/property/Property;)V"))
	public void addValidateProxy(StateFactory.Builder<?, ?> obj, Property<?> property)
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
	public String validateExceptionMessageProxy(String orig)
	{
		return " with no possible values";
	}
	
	@ModifyConstant(method = "validate", constant = @Constant(ordinal = 0, intValue = 1))
	public int validateSizeProxy(int orig)
	{
		return 0;
	}
}
