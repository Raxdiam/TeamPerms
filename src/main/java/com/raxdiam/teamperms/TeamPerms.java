package com.raxdiam.teamperms;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.raxdiam.teamperms.config.Config;
import com.raxdiam.teamperms.events.ScoreboardCallbacks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
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

	@Override
	public void onInitialize() {
		ServerStartCallback.EVENT.register(minecraftServer -> {
			var commandManager = minecraftServer.getCommandManager();
			var rootNode = commandManager.getDispatcher().getRoot();
			CONFIG.teamMap.forEach((team, cmds) -> cmds.forEach(cmd -> {
				CommandNodeHelper.changeRequirement(rootNode, cmd, createTeamPredicate(team));
			}));

			ScoreboardCallbacks.TEAM_JOIN.register((playerName, team) -> {
				commandManager.sendCommandTree(minecraftServer.getPlayerManager().getPlayer(playerName));
				return false;
			});

			ScoreboardCallbacks.TEAM_LEAVE.register((playerName, team) -> {
				commandManager.sendCommandTree(minecraftServer.getPlayerManager().getPlayer(playerName));
				return false;
			});
		});
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
