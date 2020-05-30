package virtuoel.statement.api.property;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;

import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;

public class IdentifierProperty extends Property<Identifier> implements MutableProperty<Identifier>
{
	private final Collection<Identifier> values = new LinkedHashSet<>();
	
	public IdentifierProperty(String name)
	{
		super(name, Identifier.class);
	}
	
	@Override
	public Collection<Identifier> getValues()
	{
		return values;
	}
	
	@Override
	public Optional<Identifier> parse(final String valueName)
	{
		final int underscoreIndex = valueName.lastIndexOf('_');
		if (underscoreIndex != -1)
		{
			try
			{
				final int namespaceLength = Integer.parseInt(valueName.substring(underscoreIndex + 1));
				return Optional.of(new Identifier(valueName.substring(0, namespaceLength), valueName.substring(namespaceLength + 1, underscoreIndex)));
			}
			catch (NumberFormatException e)
			{
				
			}
		}
		
		return Optional.empty();
	}
	
	@Override
	public String name(Identifier value)
	{
		return value.getNamespace() + "_" + value.getPath() + "_" + value.getNamespace().length();
	}
}
