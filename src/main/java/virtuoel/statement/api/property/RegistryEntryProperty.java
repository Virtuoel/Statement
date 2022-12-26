package virtuoel.statement.api.property;

import java.util.Optional;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import virtuoel.statement.util.RegistryUtils;

public class RegistryEntryProperty<T> extends IdentifierProperty
{
	private final Registry<T> registry;
	private final Identifier defaultId;
	
	public RegistryEntryProperty(String name, Registry<T> registry)
	{
		super(name);
		
		this.registry = registry;
		
		this.defaultId = RegistryUtils.getDefaultId(this.registry);
		
		if (this.defaultId != null)
		{
			getValues().add(this.defaultId);
		}
	}
	
	public Registry<T> getRegistry()
	{
		return registry;
	}
	
	@Override
	public Optional<Identifier> parse(final String valueName)
	{
		return Optional.ofNullable(super.parse(valueName)
			.flatMap(id -> RegistryUtils.getOrEmpty(registry, id))
			.map(e -> RegistryUtils.getId(registry, e))
			.orElse(defaultId));
	}
}
