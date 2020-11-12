package virtuoel.statement.mixin.compat116plus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.serialization.MapCodec;

import net.minecraft.state.State;

@Mixin(State.class)
public interface StateAccessor<S>
{
	@Accessor
	MapCodec<S> getCodec();
	
	@Accessor
	void setCodec(MapCodec<S> codec);
}
