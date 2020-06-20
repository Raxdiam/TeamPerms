package com.raxdiam.teamperms.events;

import net.fabricmc.fabric.api.event.Event;

import static com.raxdiam.teamperms.events.TeamModifyEvent.event;

public class ScoreboardCallbacks {
    public static final Event<TeamModifyCallback> TEAM_JOIN = event();
    public static final Event<TeamModifyCallback> TEAM_LEAVE = event();
}
