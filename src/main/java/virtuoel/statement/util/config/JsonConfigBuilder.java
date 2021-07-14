package virtuoel.statement.util.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonConfigBuilder extends ConfigBuilder<JsonObject, JsonElement, JsonConfigHandler>
{
	public JsonConfigBuilder(final String namespace, final String path)
	{
		super(namespace, path);
	}
	
	@Override
	public <T extends Number> Supplier<T> numberConfig(final String member, Function<Number, T> mapper, T defaultValue)
	{
		return customConfig(
			c -> c.addProperty(member, defaultValue),
			config -> () -> Optional.ofNullable(config.get().get(member))
				.filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsJsonPrimitive)
				.filter(JsonPrimitive::isNumber).map(JsonPrimitive::getAsNumber)
				.map(mapper).orElse(defaultValue)
		);
	}
	
	@Override
	public Supplier<Boolean> booleanConfig(final String member, final boolean defaultValue)
	{
		return customConfig(
			c -> c.addProperty(member, defaultValue),
			config -> () -> Optional.ofNullable(config.get().get(member))
				.filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsJsonPrimitive)
				.filter(JsonPrimitive::isBoolean).map(JsonPrimitive::getAsBoolean)
				.orElse(defaultValue)
		);
	}
	
	@Override
	public Supplier<String> stringConfig(String member, String defaultValue)
	{
		return customConfig(
			c -> c.addProperty(member, defaultValue),
			config -> () -> Optional.ofNullable(config.get().get(member))
				.filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsString)
				.orElse(defaultValue)
		);
	}
	
	@Override
	public Supplier<List<String>> stringListConfig(final String config)
	{
		return listConfig(config, JsonElement::getAsString);
	}
	
	@Override
	public <T> Supplier<List<T>> listConfig(final String member, final Function<JsonElement, T> mapper)
	{
		return customConfig(
			c -> c.add(member, new JsonArray()),
			config -> () -> Optional.ofNullable(config.get().get(member))
				.filter(JsonElement::isJsonArray).map(JsonElement::getAsJsonArray)
				.map(JsonArray::spliterator).map(a -> StreamSupport.stream(a, false))
				.map(s -> s.map(mapper).collect(Collectors.toList()))
				.orElseGet(ArrayList::new)
		);
	}
	
	@Override
	protected JsonConfigHandler createConfig()
	{
		return new JsonConfigHandler(
			namespace,
			path,
			() -> populateDefaults(new JsonObject())
		);
	}
}
