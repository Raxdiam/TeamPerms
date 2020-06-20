package com.raxdiam.teamperms.events;

import net.minecraft.scoreboard.Team;

public interface TeamCallback {
    boolean handle(Team team);
}
