package virtuoel.statement.api;

import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import virtuoel.kanos_config.api.JsonConfigBuilder;
import virtuoel.statement.Statement;

public class StatementConfig
{
	@ApiStatus.Internal
	public static final JsonConfigBuilder BUILDER = new JsonConfigBuilder(
		StatementApi.MOD_ID,
		FabricLoader.getInstance().getConfigDir().resolve(StatementApi.MOD_ID).resolve("config.json").normalize()
	);
	
	public static final Client CLIENT = new Client(BUILDER);
	public static final Common COMMON = new Common(BUILDER);
	public static final Server SERVER = new Server(BUILDER);
	
	public static final class Common
	{
		public final Supplier<Boolean> enableStateDeferralApi;
		public final Supplier<Boolean> enableIdSyncApi;
		public final Supplier<Boolean> forceParallelMode;
		public final Supplier<Set<BlockState>> customBlockStateDeferral;
		public final Supplier<Map<BlockState, OptionalInt>> customBlockStateSync;
		public final Supplier<Set<FluidState>> customFluidStateDeferral;
		public final Supplier<Map<FluidState, OptionalInt>> customFluidStateSync;
		
		private Common(final JsonConfigBuilder builder)
		{
			this.enableStateDeferralApi = builder.booleanConfig("enableStateDeferralApi", true);
			this.enableIdSyncApi = builder.booleanConfig("enableIdSyncApi", true);
			this.forceParallelMode = builder.booleanConfig("forceParallelMode", false);
			
			this.customBlockStateDeferral = Statement.createSetConfig(
				builder,
				"customBlockStateDeferral",
				Statement::createBlockStateDeferralConfig
			);
			this.customBlockStateSync = Statement.createMapConfig(
				builder,
				"customBlockStateSync",
				Statement::createBlockStateSyncConfig
			);
			
			this.customFluidStateDeferral = Statement.createSetConfig(
				builder,
				"customFluidStateDeferral",
				Statement::createFluidStateDeferralConfig
			);
			this.customFluidStateSync = Statement.createMapConfig(
				builder,
				"customFluidStateSync",
				Statement::createFluidStateSyncConfig
			);
		}
	}
	
	public static final class Client
	{
		private Client(final JsonConfigBuilder builder)
		{
			
		}
	}
	
	public static final class Server
	{
		private Server(final JsonConfigBuilder builder)
		{
			
		}
	}
	
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "5.0.0")
	public static final Supplier<com.google.gson.JsonObject> HANDLER = BUILDER.config;
	
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "5.0.0")
	public static final com.google.gson.JsonObject DATA = BUILDER.config.get();
	
	private StatementConfig()
	{
		
	}
}
