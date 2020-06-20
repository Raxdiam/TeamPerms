package com.raxdiam.teamperms.events;

import net.minecraft.scoreboard.Team;

public interface TeamPlayerCallback {
    boolean handle(String playerName, Team team);
}
