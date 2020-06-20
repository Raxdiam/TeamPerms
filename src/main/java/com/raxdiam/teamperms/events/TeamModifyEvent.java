package com.raxdiam.teamperms.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class TeamModifyEvent {
    private TeamModifyEvent() {
    }

    public static Event<TeamModifyCallback> event() {
        return EventFactory.createArrayBacked(TeamModifyCallback.class, handlers -> (playerName, team) -> {
            for (var handler : handlers) {
                if (handler.handle(playerName, team)) return true;
            }
            return false;
        });
    }
}
