package virtuoel.statement.util;

import java.util.Collection;
import java.util.Optional;

public interface StatementPropertyExtensions<T>
{
	String statement_getName();
	Collection<T> statement_getValues();
	String statement_name(T value);
	Optional<T> statement_parse(String name);
}
