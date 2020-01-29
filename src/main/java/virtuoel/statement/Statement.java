package virtuoel.statement;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.IdList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import virtuoel.statement.api.StatementApi;
import virtuoel.statement.api.StatementConfig;
import virtuoel.statement.util.StateIdListSync;

public class Statement implements ModInitializer, StatementApi
{
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	
	@Override
	public void onInitialize()
	{
		StatementConfig.DATA.getClass();
		
		final boolean fabricCommandsLoaded = FabricLoader.getInstance().isModLoaded("fabric-commands-v0");
		final boolean fabricNetworkingLoaded = FabricLoader.getInstance().isModLoaded("fabric-networking-v0");
		
		if (fabricCommandsLoaded)
		{
			StateIdListSync.setupCommands(fabricNetworkingLoaded);
		}
		
		if (fabricNetworkingLoaded)
		{
			StateIdListSync.setupServerNetworking();
		}
	}
	
	public static Identifier id(String name)
	{
		return new Identifier(MOD_ID, name);
	}
	
	public static final Identifier CLIENT_STATES_PACKET = id("client_states");
	
	public static Optional<Integer> getSyncedBlockStateId(@Nullable final BlockState state)
	{
		return getSyncedStateId(Block.STATE_IDS, state);
	}
	
	public static Optional<Integer> getSyncedFluidStateId(@Nullable final FluidState state)
	{
		return getSyncedStateId(Fluid.STATE_IDS, state);
	}
	
	public static <S> Optional<Integer> getSyncedStateId(final IdList<S> idList, @Nullable final S state)
	{
		if (state != null)
		{
			final boolean enableIdSyncApi = Optional.ofNullable(StatementConfig.DATA.get("enableIdSyncApi"))
				.filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsJsonPrimitive)
				.filter(JsonPrimitive::isBoolean).map(JsonPrimitive::getAsBoolean)
				.orElse(true);
			
			if (enableIdSyncApi)
			{
				Optional<Integer> syncedId;
				
				for (final StatementApi api : StatementApi.ENTRYPOINTS)
				{
					syncedId = api.getSyncedId(idList, state);
					
					if (syncedId.isPresent())
					{
						return syncedId;
					}
				}
			}
			else if (idList == Block.STATE_IDS)
			{
				return BLOCK_STATE_SYNC_DATA.get().getOrDefault(state, Optional.empty());
			}
			else if (idList == Fluid.STATE_IDS)
			{
				return FLUID_STATE_SYNC_DATA.get().getOrDefault(state, Optional.empty());
			}
		}
		
		return Optional.empty();
	}
	
	private static final Lazy<Map<BlockState, Optional<Integer>>> BLOCK_STATE_SYNC_DATA = new Lazy<>(() ->
	{
		final JsonObject data = Optional.ofNullable(StatementConfig.DATA.get("customBlockStateSync"))
			.filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject)
			.orElseGet(JsonObject::new);
		
		return loadStateSyncData(data, Registry.BLOCK, Block::getStateManager);
	});
	
	private static final Lazy<Map<FluidState, Optional<Integer>>> FLUID_STATE_SYNC_DATA = new Lazy<>(() ->
	{
		final JsonObject data = Optional.ofNullable(StatementConfig.DATA.get("customFluidStateSync"))
			.filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject)
			.orElseGet(JsonObject::new);
		
		return loadStateSyncData(data, Registry.FLUID, Fluid::getStateManager);
	});
	
	private static <O, S extends State<S>> Map<S, Optional<Integer>> loadStateSyncData(final JsonObject data, final DefaultedRegistry<O> registry, final Function<O, StateManager<O, S>> managerFunc)
	{
		final Map<S, Optional<Integer>> syncData = new HashMap<>();
		
		for (final Entry<String, JsonElement> e : data.entrySet())
		{
			registry.getOrEmpty(new Identifier(e.getKey())).ifPresent(block ->
			{
				final JsonArray states = Optional.ofNullable(e.getValue())
					.filter(JsonElement::isJsonArray).map(JsonElement::getAsJsonArray)
					.orElseGet(JsonArray::new);
				
				final StateManager<O, S> manager = managerFunc.apply(block);
				
				for (final JsonElement s : states)
				{
					if (!s.isJsonObject())
					{
						continue;
					}
					
					final JsonObject stateSyncData = s.getAsJsonObject();
					
					final JsonObject properties = Optional.ofNullable(stateSyncData.get("properties"))
						.filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject)
						.orElseGet(JsonObject::new);
					
					final Optional<Integer> syncedId = Optional.ofNullable(stateSyncData.get("syncedId"))
						.filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsJsonPrimitive)
						.filter(JsonPrimitive::isNumber).map(JsonPrimitive::getAsInt);
					
					final Map<Property<?>, Predicate<Object>> predicates = new HashMap<>();
					
					for (final Entry<String, JsonElement> p : properties.entrySet())
					{
						final Property<?> property = manager.getProperty(p.getKey());
						if (property != null)
						{
							property.parse(p.getValue().getAsString()).ifPresent(val -> predicates.put(property, ((Object) val)::equals));
						}
					}
					
					manager.getStates().stream()
						.filter(st -> predicates.entrySet().stream().allMatch(en -> en.getValue().test(st.get(en.getKey()))))
						.forEach(st -> syncData.put(st, syncedId));
				}
			});
		}
		
		return syncData;
	}
	
	@Override
	public <S> Optional<Integer> getSyncedId(IdList<S> idList, @Nullable S state)
	{
		if (idList == Block.STATE_IDS)
		{
			return BLOCK_STATE_SYNC_DATA.get().getOrDefault(state, Optional.empty());
		}
		else if (idList == Fluid.STATE_IDS)
		{
			return FLUID_STATE_SYNC_DATA.get().getOrDefault(state, Optional.empty());
		}
		
		return StatementApi.super.getSyncedId(idList, state);
	}
}
