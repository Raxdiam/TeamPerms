package com.raxdiam.teamperms.events;

import net.minecraft.scoreboard.Team;

public interface TeamModifyCallback {
    boolean handle(String playerName, Team team);
}
