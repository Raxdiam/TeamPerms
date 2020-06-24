package com.raxdiam.teamperms.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.raxdiam.teamperms.TeamPerms;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Predicate;

public class PermissionCommand {
    private static final Logger LOGGER = LogManager.getLogger(TeamPerms.MOD_NAME);

    private final PermissionManager manager;
    private final PermissionTeam permTeam;
    private final String name;
    private final int level;
    private final Predicate<?> requirement;

    public PermissionCommand(PermissionManager manager, PermissionTeam permTeam, String name, int level) {
        this.manager = manager;
        this.permTeam = permTeam;
        this.name = name;
        this.level = level;
        this.requirement = createRequirement();
    }

    public PermissionTeam getPermTeam() {
        return this.permTeam;
    }

    public String getName() {
        return this.name;
    }

    public int getLevel() {
        return this.level;
    }

    public Predicate<?> getRequirement() {
        return this.requirement;
    }

    private Predicate<?> createRequirement() {
        return o -> {
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
                var permTeam = manager.getPermTeam(team.getName());
                var level = team == null || permTeam == null ? PermissionManager.MAX_PERM_LEVEL : permTeam.level;

                return level <= this.level;
            }
            return false;
        };
    }
}
