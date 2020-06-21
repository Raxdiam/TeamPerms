package com.raxdiam.teamperms.util;

import com.mojang.brigadier.tree.RootCommandNode;
import com.raxdiam.teamperms.TeamPerms;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.LinkedList;

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

        for (var team : permTeams) {
            LOGGER.info("Team: " + team.name + ", Level: " + team.level);
        }

    }

    public void apply() {
        for (var node : rootNode.getChildren()) {
            var perm = commands.get(node.getName());
            if (perm == null) continue;

            CommandNodeHelper.changeRequirement(node, perm.getRequirement());
        }
    }

    public PermissionTeam getPermTeam(String name) {
        for (var team : permTeams) {
            if (team.name.equalsIgnoreCase(name)) return team;
        }
        return null;
    }

    private void createPermTeams(TeamMap teamMap) {
        for (var team : teamMap.entrySet()) {
            var name = team.getKey();
            permTeams.add(new PermissionTeam(this, name, teamMap.indexOf(name), team.getValue()));
        }
    }

    private void createCommands() {
        for (var permTeam : permTeams) {
            for (var command : permTeam.commands) {
                commands.put(command.getName(), command);
            }
        }
    }
}
