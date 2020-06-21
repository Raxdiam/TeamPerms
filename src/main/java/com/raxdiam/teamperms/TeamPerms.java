package com.raxdiam.teamperms;

import com.mojang.brigadier.tree.RootCommandNode;
import com.raxdiam.teamperms.config.Config;
import com.raxdiam.teamperms.events.ScoreboardCallbacks;
import com.raxdiam.teamperms.events.TeamPlayerCallback;
import com.raxdiam.teamperms.util.PermissionManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TeamPerms implements ModInitializer {
	public static final Config CONFIG = Config.load();
	public static final String MOD_NAME = "TeamPerms";

	private static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	public static CommandManager COMMAND_MANAGER;
	public static PlayerManager PLAYER_MANAGER;
	public static RootCommandNode<ServerCommandSource> ROOT_NODE;

	public static PermissionManager PERM_MANAGER;

	@Override
	public void onInitialize() {
		ServerStartCallback.EVENT.register(minecraftServer -> onServerStart(minecraftServer));
	}

	private void onServerStart(MinecraftServer minecraftServer) {
		COMMAND_MANAGER = minecraftServer.getCommandManager();
		PLAYER_MANAGER = minecraftServer.getPlayerManager();
		ROOT_NODE = COMMAND_MANAGER.getDispatcher().getRoot();

		PERM_MANAGER = new PermissionManager(CONFIG.teamCommands, minecraftServer);
		PERM_MANAGER.apply();

		var leaveJoinCallback = (TeamPlayerCallback) (playerName, team) -> {
			safeSendCommandTree(playerName, PLAYER_MANAGER, COMMAND_MANAGER);
			return false;
		};

		ScoreboardCallbacks.TEAM_JOIN.register(leaveJoinCallback);
		ScoreboardCallbacks.TEAM_LEAVE.register(leaveJoinCallback);
		ScoreboardCallbacks.TEAM_REMOVE_AFTER.register(team -> {
			var players = team.getPlayerList();
			for (var playerName : players) {
				safeSendCommandTree(playerName, PLAYER_MANAGER, COMMAND_MANAGER);
			}
			return false;
		});
	}

	private static void safeSendCommandTree(String playerName, PlayerManager playerManager, CommandManager commandManager) {
		var player = playerManager.getPlayer(playerName);
		if (player != null) commandManager.sendCommandTree(player);
	}
}
