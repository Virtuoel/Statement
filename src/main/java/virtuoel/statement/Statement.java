package virtuoel.statement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

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
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import virtuoel.kanos_config.api.InvalidatableLazySupplier;
import virtuoel.kanos_config.api.JsonConfigHandler;
import virtuoel.statement.api.StatementApi;
import virtuoel.statement.api.StatementConfig;
import virtuoel.statement.util.FabricApiCompatibility;

public class Statement implements ModInitializer, StatementApi
{
	public static final String MOD_ID = StatementApi.MOD_ID;
	
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	
	public Statement()
	{
		StatementConfig.BUILDER.config.get();
	}
	
	@Override
	public void onInitialize()
	{
		final boolean fabricCommandsLoaded = FabricLoader.getInstance().isModLoaded("fabric-command-api-v1");
		final boolean fabricNetworkingLoaded = FabricLoader.getInstance().isModLoaded("fabric-networking-v0");
		final boolean fabricRegistrySyncLoaded = FabricLoader.getInstance().isModLoaded("fabric-registry-sync-v0");
		
		if (fabricCommandsLoaded)
		{
			FabricApiCompatibility.setupCommands(fabricNetworkingLoaded);
		}
		
		if (fabricNetworkingLoaded)
		{
			FabricApiCompatibility.setupServerNetworking();
		}
		
		if (fabricRegistrySyncLoaded)
		{
			FabricApiCompatibility.setupIdRemapCallbacks();
		}
	}
	
	public static Identifier id(String name)
	{
		return new Identifier(MOD_ID, name);
	}
	
	public static final Identifier BLOCK_STATE_VALIDATION_PACKET = id("block_state_validation");
	public static final Identifier FLUID_STATE_VALIDATION_PACKET = id("fluid_state_validation");
	
	public static Supplier<Set<BlockState>> createBlockStateDeferralConfig(final JsonConfigHandler config)
	{
		return InvalidatableLazySupplier.of(() -> loadStateDeferralData(config, "customBlockStateDeferral", Registry.BLOCK, Block::getStateManager));
	}
	
	public static Supplier<Set<FluidState>> createFluidStateDeferralConfig(final JsonConfigHandler config)
	{
		return InvalidatableLazySupplier.of(() -> loadStateDeferralData(config, "customFluidStateDeferral", Registry.FLUID, Fluid::getStateManager));
	}
	
	private static <O, S extends State<O, S>> Set<S> loadStateDeferralData(final JsonConfigHandler config, final String member, final DefaultedRegistry<O> registry, final Function<O, StateManager<O, S>> managerFunc)
	{
		final JsonObject data = Optional.ofNullable(config.get().get(member))
			.filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject)
			.orElseGet(JsonObject::new);
		
		final Set<S> deferralData = new HashSet<>();
		
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
						.forEach(deferralData::add);
				}
			});
		}
		
		return deferralData;
	}
	
	public static <S> boolean shouldStateBeDeferred(final IdList<S> idList, final S state)
	{
		if (StatementConfig.COMMON.enableStateDeferralApi.get())
		{
			for (final StatementApi api : StatementApi.ENTRYPOINTS)
			{
				if (api.shouldDeferState(idList, state))
				{
					return true;
				}
			}
		}
		else if (idList == Block.STATE_IDS)
		{
			return StatementConfig.COMMON.customBlockStateDeferral.get().contains(state);
		}
		else if (idList == Fluid.STATE_IDS)
		{
			return StatementConfig.COMMON.customFluidStateDeferral.get().contains(state);
		}
		
		return false;
	}
	
	@Override
	public <S> boolean shouldDeferState(IdList<S> idList, S state)
	{
		if (idList == Block.STATE_IDS)
		{
			return StatementConfig.COMMON.customBlockStateDeferral.get().contains(state);
		}
		else if (idList == Fluid.STATE_IDS)
		{
			return StatementConfig.COMMON.customFluidStateDeferral.get().contains(state);
		}
		
		return StatementApi.super.shouldDeferState(idList, state);
	}
	
	public static OptionalInt getSyncedBlockStateId(@Nullable final int id)
	{
		return getSyncedStateId(Block.STATE_IDS, id);
	}
	
	public static OptionalInt getSyncedBlockStateId(@Nullable final BlockState state)
	{
		return getSyncedStateId(Block.STATE_IDS, state);
	}
	
	public static OptionalInt getSyncedFluidStateId(@Nullable final int id)
	{
		return getSyncedStateId(Fluid.STATE_IDS, id);
	}
	
	public static OptionalInt getSyncedFluidStateId(@Nullable final FluidState state)
	{
		return getSyncedStateId(Fluid.STATE_IDS, state);
	}
	
	public static <S> OptionalInt getSyncedStateId(final IdList<S> idList, final int id)
	{
		return getSyncedStateId(idList, idList.get(id));
	}
	
	public static <S> OptionalInt getSyncedStateId(final IdList<S> idList, @Nullable final S state)
	{
		if (state != null)
		{
			if (StatementConfig.COMMON.enableIdSyncApi.get())
			{
				OptionalInt syncedId;
				
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
				return StatementConfig.COMMON.customBlockStateSync.get().getOrDefault(state, OptionalInt.empty());
			}
			else if (idList == Fluid.STATE_IDS)
			{
				return StatementConfig.COMMON.customFluidStateSync.get().getOrDefault(state, OptionalInt.empty());
			}
		}
		
		return OptionalInt.empty();
	}
	
	public static Supplier<Map<BlockState, OptionalInt>> createBlockStateSyncConfig(final JsonConfigHandler config)
	{
		return InvalidatableLazySupplier.of(() -> loadStateSyncData(config, "customBlockStateSync", Block.STATE_IDS, Registry.BLOCK, Block::getStateManager, Block::getDefaultState));
	}
	
	public static Supplier<Map<FluidState, OptionalInt>> createFluidStateSyncConfig(final JsonConfigHandler config)
	{
		return InvalidatableLazySupplier.of(() -> loadStateSyncData(config, "customFluidStateSync", Fluid.STATE_IDS, Registry.FLUID, Fluid::getStateManager, Fluid::getDefaultState));
	}
	
	private static <O, S extends State<O, S>> Map<S, OptionalInt> loadStateSyncData(final JsonConfigHandler config, final String member, final IdList<S> idList, final DefaultedRegistry<O> registry, final Function<O, StateManager<O, S>> managerFunc, final Function<O, S> defaultStateFunc)
	{
		final JsonObject data = Optional.ofNullable(config.get().get(member))
			.filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject)
			.orElseGet(JsonObject::new);
		
		final Map<S, OptionalInt> syncData = new HashMap<>();
		
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
					
					final JsonObject syncedStateData = Optional.ofNullable(stateSyncData.get("syncedState"))
						.filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject)
						.orElseGet(JsonObject::new);
					
					final Optional<Identifier> syncedBlockId = Optional.ofNullable(syncedStateData.get("name"))
						.filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsString)
						.map(Identifier::new);
					
					final JsonObject syncedStateProperties = Optional.ofNullable(syncedStateData.get("properties"))
						.filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject)
						.orElseGet(JsonObject::new);
					
					final Function<S, Integer> stateIdFunc;
					
					if (syncedBlockId.isPresent())
					{
						S syncedState = syncedBlockId.map(registry::getOrEmpty)
							.orElseGet(Optional::empty)
							.map(defaultStateFunc)
							.orElse(null);
						
						if (syncedState != null)
						{
							final int id = idList.getRawId(getStateWithProperties(manager, syncedState, syncedStateProperties.entrySet()));
							stateIdFunc = state -> id;
						}
						else
						{
							stateIdFunc = state -> -1;
						}
					}
					else
					{
						stateIdFunc = state -> idList.getRawId(getStateWithProperties(manager, state, syncedStateProperties.entrySet()));
					}
					
					final OptionalInt syncedId = Optional.ofNullable(stateSyncData.get("syncedId"))
						.filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsJsonPrimitive)
						.filter(JsonPrimitive::isNumber).map(JsonPrimitive::getAsInt)
						.map(OptionalInt::of).orElseGet(OptionalInt::empty);
					
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
						.forEach(st ->
						{
							final int id = stateIdFunc.apply(st);
							syncData.put(st, id == -1 ? syncedId : OptionalInt.of(id));
						});
				}
			});
		}
		
		return syncData;
	}
	
	@SuppressWarnings("unchecked")
	private static <O, S extends State<O, S>> S getStateWithProperties(final StateManager<O, S> manager, S state, final Set<Entry<String, JsonElement>> properties)
	{
		for (final Entry<String, JsonElement> p : properties)
		{
			@SuppressWarnings("rawtypes")
			final Property property = manager.getProperty(p.getKey());
			
			if (property != null)
			{
				@SuppressWarnings("rawtypes")
				final Optional<Comparable> value = property.parse(p.getValue().getAsString());
				
				if (value.isPresent())
				{
					state = (S) state.with(property, value.get());
				}
			}
		}
		
		return state;
	}
	
	@Override
	public <S> OptionalInt getSyncedId(IdList<S> idList, @Nullable S state)
	{
		if (idList == Block.STATE_IDS)
		{
			return StatementConfig.COMMON.customBlockStateSync.get().getOrDefault(state, OptionalInt.empty());
		}
		else if (idList == Fluid.STATE_IDS)
		{
			return StatementConfig.COMMON.customFluidStateSync.get().getOrDefault(state, OptionalInt.empty());
		}
		
		return StatementApi.super.getSyncedId(idList, state);
	}
	
	public static void invalidateCustomStateData(final IdList<?> idList)
	{
		if (idList == Block.STATE_IDS)
		{
			InvalidatableLazySupplier.of(StatementConfig.COMMON.customBlockStateDeferral).invalidate();
			InvalidatableLazySupplier.of(StatementConfig.COMMON.customBlockStateSync).invalidate();
		}
		else if (idList == Fluid.STATE_IDS)
		{
			InvalidatableLazySupplier.of(StatementConfig.COMMON.customFluidStateDeferral).invalidate();
			InvalidatableLazySupplier.of(StatementConfig.COMMON.customFluidStateSync).invalidate();
		}
	}
}
