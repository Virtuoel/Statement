package virtuoel.statement.util.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ConfigBuilder<R, E, H extends ConfigHandler<R>>
{
	protected final String namespace, path;
	private final Collection<Consumer<R>> defaultValues;
	public final H config;
	
	public ConfigBuilder(final String namespace, final String path)
	{
		this.namespace = namespace;
		this.path = path;
		this.defaultValues = new ArrayList<>();
		this.config = createConfig();
	}
	
	public final Supplier<Double> doubleConfig(final String config, final double defaultValue)
	{
		return numberConfig(config, Number::doubleValue, defaultValue);
	}
	
	public final Supplier<Float> floatConfig(final String config, final float defaultValue)
	{
		return numberConfig(config, Number::floatValue, defaultValue);
	}
	
	public final Supplier<Long> longConfig(final String config, final long defaultValue)
	{
		return numberConfig(config, Number::longValue, defaultValue);
	}
	
	public final Supplier<Byte> byteConfig(final String config, final byte defaultValue)
	{
		return numberConfig(config, Number::byteValue, defaultValue);
	}
	
	public final Supplier<Short> shortConfig(final String config, final short defaultValue)
	{
		return numberConfig(config, Number::shortValue, defaultValue);
	}
	
	public final Supplier<Integer> intConfig(final String config, final int defaultValue)
	{
		return numberConfig(config, Number::intValue, defaultValue);
	}
	
	public abstract <T extends Number> Supplier<T> numberConfig(final String member, final Function<Number, T> mapper, final T defaultValue);
	
	public abstract Supplier<Boolean> booleanConfig(final String member, final boolean defaultValue);
	
	public abstract Supplier<String> stringConfig(final String member, final String defaultValue);
	
	public abstract Supplier<List<String>> stringListConfig(final String config);
	
	public abstract <T> Supplier<List<T>> listConfig(final String member, final Function<E, T> mapper);
	
	public final <T> Supplier<T> customConfig(final Consumer<R> defaultValue, final Function<H, Supplier<T>> entryFunction)
	{
		defaultValues.add(defaultValue);
		
		final InvalidatableLazySupplier<T> entry = InvalidatableLazySupplier.of(entryFunction.apply(config));
		
		config.addInvalidationListener(entry::invalidate);
		
		return entry;
	}
	
	protected final R populateDefaults(final R defaultConfig)
	{
		for (final Consumer<R> value : defaultValues)
		{
			value.accept(defaultConfig);
		}
		
		return defaultConfig;
	}
	
	protected abstract H createConfig();
}
