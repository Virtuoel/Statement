package virtuoel.statement.mixin.compat116plus.compat117minus;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.serialization.MapCodec;

import net.minecraft.state.State;

@Mixin(State.class)
public abstract class StateMixin<O, S>
{
	@Redirect(method = "method_28497", remap = false, at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/MapCodec;fieldOf(Ljava/lang/String;)Lcom/mojang/serialization/MapCodec;"))
	private static <O, S> MapCodec<S> fieldOfProxy(MapCodec<S> c, String string, Function<O, S> ownerToStateFunction, O object)
	{
		return c.codec().optionalFieldOf(string, ownerToStateFunction.apply(object));
	}
}
