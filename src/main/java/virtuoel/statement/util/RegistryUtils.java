package virtuoel.statement.util;

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class RegistryUtils
{
	public static final Registry<Block> BLOCK_REGISTRY;
	public static final Registry<Item> ITEM_REGISTRY;
	public static final Registry<Fluid> FLUID_REGISTRY;
	
	static
	{
		BLOCK_REGISTRY = Registries.BLOCK;
		ITEM_REGISTRY = Registries.ITEM;
		FLUID_REGISTRY = Registries.FLUID;
	}
	
	public static <V> V get(Registry<V> registry, Identifier id)
	{
		return registry.get(id);
	}
	
	public static <V> Identifier getId(Registry<V> registry, V entry)
	{
		return registry.getId(entry);
	}
	
	public static <V> int getRawId(Registry<V> registry, V entry)
	{
		return registry.getRawId(entry);
	}
	
	public static <V> Optional<V> getOrEmpty(Registry<V> registry, Identifier id)
	{
		return registry.getOrEmpty(id);
	}
	
	public static <V> Identifier getDefaultId(Registry<V> registry)
	{
		if (registry instanceof DefaultedRegistry)
		{
			return ((DefaultedRegistry<?>) registry).getDefaultId();
		}
		
		return null;
	}
}
