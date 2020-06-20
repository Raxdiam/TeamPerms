package com.raxdiam.teamperms.events;

import net.fabricmc.fabric.api.event.Event;

import static com.raxdiam.teamperms.events.TeamEvent.*;

public class ScoreboardCallbacks {
    public static final Event<TeamCallback> TEAM_UPDATE = event();
    public static final Event<TeamPlayerCallback> TEAM_JOIN = playerEvent();
    public static final Event<TeamPlayerCallback> TEAM_LEAVE = playerEvent();
    public static final Event<TeamCallback> TEAM_REMOVE_BEFORE = event();
    public static final Event<TeamCallback> TEAM_REMOVE_AFTER = event();
}
