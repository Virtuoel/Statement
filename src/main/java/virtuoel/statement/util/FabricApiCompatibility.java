package virtuoel.statement.util;

import java.util.UUID;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.PacketByteBuf;
import virtuoel.statement.Statement;

public class FabricApiCompatibility
{
	public static void setupCommands(final boolean networkingLoaded)
	{
		CommandRegistry.INSTANCE.register(false, commandDispatcher ->
		{
			commandDispatcher.register(
				CommandManager.literal("statement")
				.requires(commandSource ->
				{
					return commandSource.hasPermissionLevel(2);
				})
				.then(
					CommandManager.literal("debug")
					.then(
						CommandManager.literal("test_sync")
						.executes(context ->
						{
							return execute(context, networkingLoaded, context.getSource().getPlayer(), 100, 0);
						})
						.then(
							CommandManager.argument("player", EntityArgumentType.player())
							.executes(context ->
							{
								return execute(context, networkingLoaded, EntityArgumentType.getPlayer(context, "player"), 100, 0);
							})
							.then(
								CommandManager.argument("rate", IntegerArgumentType.integer(1, Block.STATE_IDS.size()))
								.executes(context ->
								{
									return execute(context, networkingLoaded, EntityArgumentType.getPlayer(context, "player"), IntegerArgumentType.getInteger(context, "rate"), 0);
								})
								.then(
									CommandManager.argument("start_id", IntegerArgumentType.integer(0, Block.STATE_IDS.size() - 1))
									.executes(context ->
									{
										return execute(context, networkingLoaded, EntityArgumentType.getPlayer(context, "player"), IntegerArgumentType.getInteger(context, "rate"), IntegerArgumentType.getInteger(context, "start_id"));
									})
								)
							)
						)
					)
				)
			);
		});
	}
	
	private static int execute(final CommandContext<ServerCommandSource> context, final boolean networkingLoaded, final PlayerEntity player, final int rate, final int initialId) throws CommandSyntaxException
	{
		if (networkingLoaded)
		{
			if (ServerSidePacketRegistry.INSTANCE.canPlayerReceive(player, Statement.CLIENT_STATES_PACKET))
			{
				final PlayerEntity executor = context.getSource().getPlayer();
				final PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer()).writeUuid(executor.getUuid()).writeVarInt(rate);
				
				for (int i = 0; i < rate; i++)
				{
					buffer.writeVarInt(initialId + i);
				}
				
				context.getSource().sendFeedback(new LiteralText("Running state sync tests..."), false);
				
				ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, Statement.CLIENT_STATES_PACKET, buffer);
				
				return 1;
			}
			else
			{
				context.getSource().sendFeedback(new LiteralText("Error: Target player cannot receive sync test packet."), false);
				return 0;
			}
		}
		else
		{
			context.getSource().sendFeedback(new LiteralText("Fabric Networking not found on server."), false);
			return 0;
		}
	}
	
	public static void setupServerNetworking()
	{
		ServerSidePacketRegistry.INSTANCE.register(Statement.CLIENT_STATES_PACKET, (ctx, buf) ->
		{
			final PlayerEntity player = ctx.getPlayer();
			
			final UUID uuid = buf.readUuid();
			
			final int idQuantity = buf.readVarInt();
			
			if (idQuantity == 0)
			{
				return;
			}
			
			final int[] ids = new int[idQuantity];
			final String[] snbts = new String[idQuantity];
			
			for (int i = 0; i < idQuantity; i++)
			{
				ids[i] = buf.readVarInt();
				snbts[i] = buf.readString(32767);
			}
			
			ctx.getTaskQueue().execute(() ->
			{
				final PlayerEntity executor = player.getEntityWorld().getPlayerByUuid(uuid);
				
				if (executor.onGround || !executor.isSneaking())
				{
					boolean idsFound = false;
					boolean done = false;
					
					for (int i = 0; i < idQuantity; i++)
					{
						final BlockState state = Block.STATE_IDS.get(ids[i]);
						
						try
						{
							final CompoundTag sentData = StringNbtReader.parse(snbts[i]);
							
							if (state != null)
							{
								final CompoundTag ownData = NbtHelper.fromBlockState(state);
								
								if (sentData.equals(ownData))
								{
									final int total = Block.STATE_IDS.size();
									final float percent = ((float) (ids[i] + 1) / total) * 100;
									
									executor.sendMessage(new LiteralText(String.format("ID %d matched (%d/%d: %.2f%%):\n%s", ids[i], ids[i] + 1, total, percent, ownData)));
								}
								else
								{
									executor.sendMessage(new LiteralText(String.format("ID %d mismatched!\nServer state:\n%s\nClient state:\n%s", ids[i], ownData, sentData)));
								}
								
								idsFound = true;
							}
							else
							{
								executor.sendMessage(new LiteralText("Received ID not found on server: " + ids[i]));
							}
						}
						catch (CommandSyntaxException e)
						{
							if (state == null)
							{
								executor.sendMessage(new LiteralText("Done matching after " + ids[i] + " blockstates"));
								done = true;
								break;
							}
							executor.sendMessage(new LiteralText("Failed to parse received state from SNBT:\n" + snbts[i]));
						}
					}
					
					if (!done && idsFound)
					{
						if (ServerSidePacketRegistry.INSTANCE.canPlayerReceive(player, Statement.CLIENT_STATES_PACKET))
						{
							final PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer()).writeUuid(uuid).writeVarInt(idQuantity);
							
							for (int i = 0; i < idQuantity; i++)
							{
								buffer.writeVarInt(ids[i] + idQuantity);
							}
							
							ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, Statement.CLIENT_STATES_PACKET, buffer);
						}
						else
						{
							executor.sendMessage(new LiteralText("Error: Target player cannot receive sync packet."));
						}
					}
				}
			});
		});
	}
	
	public static void setupClientNetworking()
	{
		ClientSidePacketRegistry.INSTANCE.register(Statement.CLIENT_STATES_PACKET, (ctx, buf) ->
		{
			final PlayerEntity player = ctx.getPlayer();
			
			final UUID uuid = buf.readUuid();
			final int idQuantity = buf.readVarInt();
			
			if (idQuantity == 0)
			{
				return;
			}
			
			final int[] ids = new int[idQuantity];
			
			for (int i = 0; i < idQuantity; i++)
			{
				ids[i] = buf.readVarInt();
			}
			
			ctx.getTaskQueue().execute(() ->
			{
				if (player.onGround || !player.isSneaking())
				{
					final PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer()).writeUuid(uuid).writeVarInt(idQuantity);
					
					for (int i = 0; i < idQuantity; i++)
					{
						final BlockState state = Block.STATE_IDS.get(ids[i]);
						final String snbt = state == null ? "No state found on client for ID " + ids[i] : NbtHelper.fromBlockState(state).toString();
						
						buffer.writeVarInt(ids[i]).writeString(snbt);
					}
					
					ClientSidePacketRegistry.INSTANCE.sendToServer(Statement.CLIENT_STATES_PACKET, buffer);
				}
			});
		});
	}
}
