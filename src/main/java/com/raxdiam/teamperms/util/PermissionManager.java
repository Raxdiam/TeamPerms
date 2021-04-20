package com.raxdiam.teamperms.util;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.raxdiam.teamperms.TeamPerms;
import com.raxdiam.teamperms.events.ScoreboardCallbacks;
import com.raxdiam.teamperms.events.TeamPlayerCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class PermissionManager {
    private static final Logger LOGGER = LogManager.getLogger(TeamPerms.MOD_NAME);

    public static int MAX_PERM_LEVEL;

    private final CommandManager commandManager;
    private final PlayerManager playerManager;
    private final RootCommandNode<ServerCommandSource> rootNode;
    private final LinkedList<PermissionTeam> permTeams;

    public final LinkedHashMap<String, PermissionCommand> commands;

    public PermissionManager(TeamMap teamMap, MinecraftServer minecraftServer) {
        MAX_PERM_LEVEL = teamMap.size() - 1;
        commandManager = minecraftServer.getCommandManager();
        playerManager = minecraftServer.getPlayerManager();
        rootNode = commandManager.getDispatcher().getRoot();
        permTeams = new LinkedList<>();
        commands = new LinkedHashMap<>();

        createPermTeams(teamMap);
        createCommands();
    }

    public void apply() {
        for (CommandNode<ServerCommandSource> node : rootNode.getChildren()) {
            PermissionCommand perm = commands.get(node.getName());
            if (perm == null) continue;

            CommandNodeHelper.changeRequirement(node, perm.getRequirement());
        }

        TeamPlayerCallback leaveJoinCallback = (TeamPlayerCallback) (playerName, team) -> {
            safeSendCommandTree(playerName, playerManager, commandManager);
            return false;
        };

        ScoreboardCallbacks.TEAM_JOIN.register(leaveJoinCallback);
        ScoreboardCallbacks.TEAM_LEAVE.register(leaveJoinCallback);
        ScoreboardCallbacks.TEAM_REMOVE_AFTER.register(team -> {
            Collection<String> players = team.getPlayerList();
            for (String playerName : players) {
                safeSendCommandTree(playerName, playerManager, commandManager);
            }
            return false;
        });
    }

    public PermissionTeam getPermTeam(String name) {
        for (PermissionTeam team : permTeams) {
            if (team.name.equalsIgnoreCase(name)) return team;
        }
        return null;
    }

    private void createPermTeams(TeamMap teamMap) {
        for (Map.Entry<String, List<String>> team : teamMap.entrySet()) {
            String name = team.getKey();
            permTeams.add(new PermissionTeam(this, name, teamMap.indexOf(name), team.getValue()));
        }
    }

    private void createCommands() {
        for (PermissionTeam permTeam : permTeams) {
            for (PermissionCommand command : permTeam.commands) {
                commands.put(command.getName(), command);
            }
        }
    }

    private static void safeSendCommandTree(String playerName, PlayerManager playerManager, CommandManager commandManager) {
        ServerPlayerEntity player = playerManager.getPlayer(playerName);
        if (player != null) commandManager.sendCommandTree(player);
    }
}
