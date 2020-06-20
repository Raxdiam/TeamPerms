package com.raxdiam.teamperms.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class TeamEvent {
    private TeamEvent() {
    }

    public static Event<TeamCallback> event() {
        return EventFactory.createArrayBacked(TeamCallback.class, handlers -> team -> {
            for (var handler : handlers) {
                if (handler.handle(team)) return true;
            }
            return false;
        });
    }

    public static Event<TeamPlayerCallback> playerEvent() {
        return EventFactory.createArrayBacked(TeamPlayerCallback.class, handlers -> (playerName, team) -> {
            for (var handler : handlers) {
                if (handler.handle(playerName, team)) return true;
            }
            return false;
        });
    }
}
