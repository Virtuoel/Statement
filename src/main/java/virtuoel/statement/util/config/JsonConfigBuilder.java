package virtuoel.statement.util.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.gson.Gson;
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
	public <T extends Number> Supplier<T> numberConfig(final String name, final Function<Number, T> mapper, final T defaultValue)
	{
		return customConfig(
			name,
			config -> v -> config.get().addProperty(name, v),
			defaultValue,
			config -> () -> Optional.ofNullable(config.get().get(name))
				.filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsJsonPrimitive)
				.filter(JsonPrimitive::isNumber).map(JsonPrimitive::getAsNumber)
				.map(mapper).orElse(defaultValue)
		);
	}
	
	@Override
	public Supplier<Boolean> booleanConfig(final String name, final boolean defaultValue)
	{
		return customConfig(
			name,
			config -> v -> config.get().addProperty(name, v),
			defaultValue,
			config -> () -> Optional.ofNullable(config.get().get(name))
				.filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsJsonPrimitive)
				.filter(JsonPrimitive::isBoolean).map(JsonPrimitive::getAsBoolean)
				.orElse(defaultValue)
		);
	}
	
	@Override
	public Supplier<String> stringConfig(String name, String defaultValue)
	{
		return customConfig(
			name,
			config -> v -> config.get().addProperty(name, v),
			defaultValue,
			config -> () -> Optional.ofNullable(config.get().get(name))
				.filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsString)
				.orElse(defaultValue)
		);
	}
	
	@Override
	public Supplier<List<String>> stringListConfig(final String name)
	{
		return listConfig(name, JsonElement::getAsString);
	}
	
	@Override
	public <T> Supplier<List<T>> listConfig(final String name, final Function<JsonElement, T> mapper)
	{
		return customConfig(
			name,
			config -> v -> config.get().add(name, new Gson().toJsonTree(v).getAsJsonArray()),
			new ArrayList<>(),
			config -> () -> Optional.ofNullable(config.get().get(name))
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
