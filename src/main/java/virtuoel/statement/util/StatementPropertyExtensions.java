package virtuoel.statement.util;

import java.util.Collection;

public interface StatementPropertyExtensions<T>
{
	String statement_getName();
	Collection<T> statement_getValues();
	String statement_name(T value);
}
