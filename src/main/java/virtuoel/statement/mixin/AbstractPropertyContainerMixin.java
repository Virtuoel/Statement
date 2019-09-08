package virtuoel.statement.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.PropertyContainer;
import net.minecraft.state.property.Property;

@Mixin(AbstractPropertyContainer.class)
public abstract class AbstractPropertyContainerMixin<O, S> implements PropertyContainer<S>
{
	@Shadow @Final protected O owner;
	@Shadow @Final private ImmutableMap<Property<?>, Comparable<?>> entries;
	@Shadow private Table<Property<?>, Comparable<?>, S> withTable;
	
	@Inject(at = @At("HEAD"), method = "with", cancellable = true)
	public <T extends Comparable<T>, V extends T> void onWith(Property<T> property_1, V comparable_1, CallbackInfoReturnable<Object> info)
	{
		final Comparable<?> comparable_2 = this.entries.get(property_1);
		if(comparable_2 == null)
		{
			LOGGER.info("Cannot set property " + property_1 + " as it does not exist in " + this.owner);
			info.setReturnValue(this);
		}
		else if(comparable_2 != comparable_1 && withTable.get(property_1, comparable_1) == null)
		{
			LOGGER.info("Cannot set property " + property_1 + " to " + comparable_1 + " on " + this.owner + ", it is not an allowed value");
			info.setReturnValue(this);
		}
	}
	
	@Redirect(method = "createWithTable", at = @At(value = "FIELD", ordinal = 0, target = "Lnet/minecraft/state/AbstractPropertyContainer;withTable:Lcom/google/common/collect/Table;"))
	public Table<Property<?>, Comparable<?>, S> onCreateWithTable(AbstractPropertyContainer<O, S> this$0, Map<Map<Property<?>, Comparable<?>>, S> map_1)
	{
		return null;
	}
}
