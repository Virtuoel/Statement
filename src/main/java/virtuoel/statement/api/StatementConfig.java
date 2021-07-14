package virtuoel.statement.api;

import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus;

import com.google.gson.JsonObject;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import virtuoel.kanos_config.api.JsonConfigBuilder;
import virtuoel.statement.Statement;

public class StatementConfig
{
	@ApiStatus.Internal
	public static final JsonConfigBuilder BUILDER = new JsonConfigBuilder(
		StatementApi.MOD_ID,
		"config.json"
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
			
			this.customBlockStateDeferral = builder.customConfig(
				c -> c.add("customBlockStateDeferral", new JsonObject()),
				Statement::createBlockStateDeferralConfig
			);
			this.customBlockStateSync = builder.customConfig(
				c -> c.add("customBlockStateSync", new JsonObject()),
				Statement::createBlockStateSyncConfig
			);
			
			this.customFluidStateDeferral = builder.customConfig(
				c -> c.add("customFluidStateDeferral", new JsonObject()),
				Statement::createFluidStateDeferralConfig
			);
			this.customFluidStateSync = builder.customConfig(
				c -> c.add("customFluidStateSync", new JsonObject()),
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
