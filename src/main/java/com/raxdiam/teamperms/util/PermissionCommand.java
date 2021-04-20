package com.raxdiam.teamperms.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.raxdiam.teamperms.TeamPerms;
import net.minecraft.scoreboard.AbstractTeam;
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
            if (((ServerCommandSource) o).hasPermissionLevel(4)) return true;
            if (o instanceof ServerCommandSource) {
                ServerCommandSource s = (ServerCommandSource) o;

                ServerPlayerEntity player;
                try {
                    player = s.getPlayer();
                } catch (CommandSyntaxException e) {
                    return false;
                }
                if (player == null) return false;

                AbstractTeam team = player.getScoreboardTeam();
                if (team == null) return PermissionManager.MAX_PERM_LEVEL <= this.level;

                PermissionTeam permTeam = manager.getPermTeam(team.getName());
                if (permTeam == null) return PermissionManager.MAX_PERM_LEVEL <= this.level;

                return permTeam.level <= this.level;
            }
            return false;
        };
    }
}
