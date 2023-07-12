package virtuoel.statement.util.config;

import java.util.Objects;
import java.util.function.Supplier;

public final class InvalidatableLazySupplier<T> implements Supplier<T>
{
	private final Supplier<T> supplier;
	private boolean valid = false;
	private T value;
	
	public static <T> InvalidatableLazySupplier<T> of(final Supplier<T> delegate)
	{
		if (delegate instanceof InvalidatableLazySupplier)
		{
			return (InvalidatableLazySupplier<T>) delegate;
		}
		
		return new InvalidatableLazySupplier<>(delegate);
	}
	
	private InvalidatableLazySupplier(final Supplier<T> delegate)
	{
		this.supplier = Objects.requireNonNull(delegate);
	}
	
	public final void invalidate()
	{
		valid = false;
	}
	
	@Override
	public final T get()
	{
		if (!valid)
		{
			final T t = supplier.get();
			value = t;
			valid = true;
			return t;
		}
		
		return value;
	}
}
