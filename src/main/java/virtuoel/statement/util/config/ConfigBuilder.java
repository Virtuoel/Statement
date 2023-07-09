package virtuoel.statement.util.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ConfigBuilder<R, E, H extends ConfigHandler<R>>
{
	protected final String namespace;
	protected final Path path;
	private final Collection<Consumer<Supplier<R>>> defaultValues;
	public final H config;
	
	public ConfigBuilder(final String namespace, final Path path)
	{
		this.namespace = namespace;
		this.path = path;
		this.defaultValues = new ArrayList<>();
		this.config = createConfig();
	}
	
	public final Supplier<Double> doubleConfig(final String name, final double defaultValue)
	{
		return numberConfig(name, Number::doubleValue, defaultValue);
	}
	
	public final Supplier<Float> floatConfig(final String name, final float defaultValue)
	{
		return numberConfig(name, Number::floatValue, defaultValue);
	}
	
	public final Supplier<Long> longConfig(final String name, final long defaultValue)
	{
		return numberConfig(name, Number::longValue, defaultValue);
	}
	
	public final Supplier<Byte> byteConfig(final String name, final byte defaultValue)
	{
		return numberConfig(name, Number::byteValue, defaultValue);
	}
	
	public final Supplier<Short> shortConfig(final String name, final short defaultValue)
	{
		return numberConfig(name, Number::shortValue, defaultValue);
	}
	
	public final Supplier<Integer> intConfig(final String name, final int defaultValue)
	{
		return numberConfig(name, Number::intValue, defaultValue);
	}
	
	public abstract <T extends Number> Supplier<T> numberConfig(final String name, final Function<Number, T> mapper, final T defaultValue);
	
	public abstract Supplier<Boolean> booleanConfig(final String name, final boolean defaultValue);
	
	public abstract Supplier<String> stringConfig(final String name, final String defaultValue);
	
	public abstract Supplier<List<String>> stringListConfig(final String name);
	
	public abstract <T> Supplier<List<T>> listConfig(final String name, final Function<E, T> mapper);
	
	public final <T> MutableConfigEntry<T> customConfig(final String name, final Function<Supplier<R>, Consumer<T>> entrySetterFunction, final T defaultValue, final Function<Supplier<R>, Supplier<T>> entryGetterFunction)
	{
		defaultValues.add(r -> entrySetterFunction.apply(r).accept(defaultValue));
		
		final InvalidatableLazySupplier<T> entry = InvalidatableLazySupplier.of(entryGetterFunction.apply(config));
		
		config.addInvalidationListener(entry::invalidate);
		
		return createConfigEntry(name, defaultValue, entry, v ->
		{
			entrySetterFunction.apply(config).accept(v);
			entry.invalidate();
		});
	}
	
	protected final Supplier<R> populateDefaults(final Supplier<R> defaultConfigFactory)
	{
		return populateDefaults(defaultConfigFactory, defaultValues);
	}
	
	protected static <R> Supplier<R> populateDefaults(final Supplier<R> defaultConfigFactory, final Collection<Consumer<Supplier<R>>> defaultValues)
	{
		return () ->
		{
			final Supplier<R> defaultConfigGetter = InvalidatableLazySupplier.of(defaultConfigFactory);
			
			for (final Consumer<Supplier<R>> value : defaultValues)
			{
				value.accept(defaultConfigGetter);
			}
			
			return defaultConfigGetter.get();
		};
	}
	
	protected abstract H createConfig();
	
	public <T> MutableConfigEntry<T> createConfigEntry(final String name, final T defaultValue, final Supplier<T> supplier, final Consumer<T> consumer)
	{
		return new MutableConfigEntry<T>()
		{
			@Override
			public T get()
			{
				return supplier.get();
			}
			
			@Override
			public void accept(final T t)
			{
				consumer.accept(t);
			}
			
			@Override
			public T getValue()
			{
				return supplier.get();
			}
			
			@Override
			public void setValue(final T value)
			{
				consumer.accept(value);
			}
		};
	}
}
