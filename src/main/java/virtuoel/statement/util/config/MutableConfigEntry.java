package virtuoel.statement.util.config;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.lang3.mutable.Mutable;

public interface MutableConfigEntry<T> extends Supplier<T>, Consumer<T>, Mutable<T>
{
	
}
