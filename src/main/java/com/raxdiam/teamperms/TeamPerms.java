package com.raxdiam.teamperms;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.raxdiam.teamperms.config.Config;
import com.raxdiam.teamperms.events.ScoreboardCallbacks;
import com.raxdiam.teamperms.events.TeamPlayerCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Predicate;

public class TeamPerms implements ModInitializer {
	public static final Config CONFIG = Config.load();
	public static final String MOD_NAME = "TeamPerms";
	private static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	private static CommandManager COMMAND_MANAGER;
	private static PlayerManager PLAYER_MANAGER;

	@Override
	public void onInitialize() {
		ServerStartCallback.EVENT.register(minecraftServer -> onServerStart(minecraftServer));
	}

	private void onServerStart(MinecraftServer minecraftServer) {
		COMMAND_MANAGER = minecraftServer.getCommandManager();
		PLAYER_MANAGER = minecraftServer.getPlayerManager();
		var rootNode = COMMAND_MANAGER.getDispatcher().getRoot();
		CONFIG.teamMap.forEach((team, cmds) -> cmds.forEach(cmd -> {
			CommandNodeHelper.changeRequirement(rootNode, cmd, createTeamPredicate(team));
		}));

		var leaveJoinCallback = (TeamPlayerCallback) (playerName, team) -> {
			safeSendCommandTree(playerName, PLAYER_MANAGER, COMMAND_MANAGER);
			return false;
		};

		// Didn't realize until now that you can't change the name of a team, so there's no need for this ¯\_(ツ)_/¯
		/*ScoreboardCallbacks.TEAM_UPDATE.register(team -> {
			var players = team.getPlayerList();
			for (var playerName : players) {
				safeSendCommandTree(playerName, PLAYER_MANAGER, COMMAND_MANAGER);
			}
			return false;
		});*/
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

	private static Predicate<?> createTeamPredicate(String teamName) {
		return o -> {
			if (teamName.equalsIgnoreCase("Default")) return true;
			if (((CommandSource) o).hasPermissionLevel(4)) return true;
			if (o instanceof ServerCommandSource) {
				var s = (ServerCommandSource) o;

				ServerPlayerEntity player;
				try {
					player = s.getPlayer();
				} catch (CommandSyntaxException e) {
					return false;
				}
				if (player == null) return false;

				var team = player.getScoreboardTeam();
				if (team == null) return false;

				return team.getName().equalsIgnoreCase(teamName);
			}
			return false;
		};
	}
}
