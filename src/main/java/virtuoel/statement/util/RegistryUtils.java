package virtuoel.statement.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.SimpleDefaultedRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;
import virtuoel.statement.Statement;

public class RegistryUtils
{
	public static final MethodHandle REGISTER, GET, GET_ID, GET_RAW_ID, GET_OR_EMPTY, GET_DEFAULT_ID;
	public static final Registry<Block> BLOCK_REGISTRY;
	public static final Registry<Item> ITEM_REGISTRY;
	public static final Registry<Fluid> FLUID_REGISTRY;
	
	static
	{
		final MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
		final Int2ObjectMap<MethodHandle> h = new Int2ObjectArrayMap<MethodHandle>();
		Object rB, rI, rF = rI = rB = null;
		
		final Lookup lookup = MethodHandles.lookup();
		String mapped = "unset";
		Method m;
		Class<?> clazz;
		Field f;
		
		try
		{
			final boolean is115Minus = VersionUtils.MINOR <= 15;
			final boolean is1192Minus = VersionUtils.MINOR < 19 || (VersionUtils.MINOR == 19 && VersionUtils.PATCH <= 2);
			
			final String registrar = "net.minecraft.class_" + (is1192Minus ? "2378" : "7923");
			
			mapped = mappingResolver.mapClassName("intermediary", registrar);
			clazz = Class.forName(mapped);
			
			mapped = mappingResolver.mapFieldName("intermediary", registrar, "field_" + (is1192Minus ? "11146" : "41175"), "Lnet/minecraft/class_" + (is1192Minus ? "2348;" : "7922;"));
			f = clazz.getField(mapped);
			rB = f.get(null);
			
			mapped = mappingResolver.mapFieldName("intermediary", registrar, "field_" + (is1192Minus ? "11142" : "41178"), "Lnet/minecraft/class_" + (is1192Minus ? "2348;" : "7922;"));
			f = clazz.getField(mapped);
			rI = f.get(null);
			
			mapped = mappingResolver.mapFieldName("intermediary", registrar, "field_" + (is1192Minus ? "11154" : "41173"), "Lnet/minecraft/class_" + (is1192Minus ? "2348;" : "7922;"));
			f = clazz.getField(mapped);
			rF = f.get(null);
			
			mapped = mappingResolver.mapMethodName("intermediary", "net.minecraft.class_2378", "method_10230", "(Lnet/minecraft/class_2378;Lnet/minecraft/class_2960;Ljava/lang/Object;)Ljava/lang/Object;");
			m = Registry.class.getMethod(mapped, Registry.class, Identifier.class, Object.class);
			h.put(0, lookup.unreflect(m));
			
			mapped = mappingResolver.mapMethodName("intermediary", "net.minecraft.class_2378", "method_10223", "(Lnet/minecraft/class_2960;)Ljava/lang/Object;");
			m = Registry.class.getMethod(mapped, Identifier.class);
			h.put(1, lookup.unreflect(m));
			
			mapped = mappingResolver.mapMethodName("intermediary", "net.minecraft.class_2378", "method_10221", "(Ljava/lang/Object;)Lnet/minecraft/class_2960;");
			m = Registry.class.getMethod(mapped, Object.class);
			h.put(2, lookup.unreflect(m));
			
			mapped = mappingResolver.mapMethodName("intermediary", (is115Minus ? "net.minecraft.class_2378" : "net.minecraft.class_2359"), is115Minus ? "method_10249" : "method_10206", "(Ljava/lang/Object;)I");
			m = (is115Minus ? Registry.class : IndexedIterable.class).getMethod(mapped, Object.class);
			h.put(3, lookup.unreflect(m));
			
			mapped = mappingResolver.mapMethodName("intermediary", "net.minecraft.class_2378", "method_17966", "(Lnet/minecraft/class_2960;)Ljava/util/Optional;");
			m = Registry.class.getMethod(mapped, Identifier.class);
			h.put(4, lookup.unreflect(m));
			
			mapped = mappingResolver.mapMethodName("intermediary", (is1192Minus ? "net.minecraft.class_2348" : "net.minecraft.class_7922"), "method_10137", "()Lnet/minecraft/class_2960;");
			m = (is1192Minus ? SimpleDefaultedRegistry.class : DefaultedRegistry.class).getMethod(mapped);
			h.put(5, lookup.unreflect(m));
		}
		catch (NoSuchMethodException | SecurityException | IllegalAccessException | ClassNotFoundException | NoSuchFieldException e)
		{
			Statement.LOGGER.error("Current name lookup: {}", mapped);
			Statement.LOGGER.catching(e);
		}
		
		REGISTER = h.get(0);
		GET = h.get(1);
		GET_ID = h.get(2);
		GET_RAW_ID = h.get(3);
		GET_OR_EMPTY = h.get(4);
		GET_DEFAULT_ID = h.get(5);
		BLOCK_REGISTRY = castRegistry(rB);
		ITEM_REGISTRY = castRegistry(rI);
		FLUID_REGISTRY = castRegistry(rF);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> Registry<T> castRegistry(Object obj)
	{
		return (Registry<T>) obj;
	}
	
	public static <V, T extends V> T register(Registry<V> registry, Identifier id, T entry)
	{
		try
		{
			return (T) REGISTER.invoke(registry, id, entry);
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static <V> V get(Registry<V> registry, Identifier id)
	{
		try
		{
			return (V) GET.invoke(registry, id);
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static <V> Identifier getId(Registry<V> registry, V entry)
	{
		try
		{
			return (Identifier) GET_ID.invoke(registry, entry);
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static <V> int getRawId(Registry<V> registry, V entry)
	{
		try
		{
			if (VersionUtils.MINOR <= 15)
			{
				return (int) GET_RAW_ID.invoke(registry, entry);
			}
			else
			{
				return (int) GET_RAW_ID.invoke((IndexedIterable<V>) registry, entry);
			}
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static <V> Optional<V> getOrEmpty(Registry<V> registry, Identifier id)
	{
		try
		{
			return (Optional<V>) GET_OR_EMPTY.invoke(registry, id);
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static <V> Identifier getDefaultId(Registry<V> registry)
	{
		try
		{
			if (VersionUtils.MINOR < 19 || (VersionUtils.MINOR == 19 && VersionUtils.PATCH <= 2))
			{
				if (registry instanceof SimpleDefaultedRegistry)
				{
					return (Identifier) GET_DEFAULT_ID.invoke((SimpleDefaultedRegistry<V>) registry);
				}
			}
			else
			{
				if (registry instanceof DefaultedRegistry)
				{
					return (Identifier) GET_DEFAULT_ID.invoke((DefaultedRegistry<V>) registry);
				}
			}
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e);
		}
		
		return null;
	}
}
