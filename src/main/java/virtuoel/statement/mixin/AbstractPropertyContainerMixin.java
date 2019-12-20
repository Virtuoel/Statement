package virtuoel.statement.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;

import net.minecraft.state.AbstractState;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;

@Mixin(AbstractState.class)
public abstract class AbstractPropertyContainerMixin<O, S> implements State<S>
{
	@Shadow @Final protected O owner;
	@Shadow @Final private ImmutableMap<Property<?>, Comparable<?>> entries;
	@Shadow private Table<Property<?>, Comparable<?>, S> withTable;
	
	@Inject(at = @At("HEAD"), method = "with", cancellable = true)
	private <T extends Comparable<T>, V extends T> void onWith(Property<T> property, V value, CallbackInfoReturnable<Object> info)
	{
		final Comparable<?> currentValue = this.entries.get(property);
		if(currentValue == null)
		{
			LOGGER.info("Cannot set property {} as it does not exist in {}", property, this.owner);
			info.setReturnValue(this);
		}
		else if(currentValue != value && withTable.get(property, value) == null)
		{
			LOGGER.info("Cannot set property {} to {} on {}, it is not an allowed value", property, value, this.owner);
			info.setReturnValue(this);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "createWithTable")
	private void onCreateWithTable(Map<Map<Property<?>, Comparable<?>>, S> map, CallbackInfo info)
	{
		withTable = null;
	}
}
