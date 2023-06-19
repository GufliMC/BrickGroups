package com.guflimc.brick.groups.spigot.api.events;

import com.guflimc.brick.groups.api.domain.Group;
import org.bukkit.event.Event;

public abstract class ClanEvent extends Event {

    private final Group team;

    public ClanEvent(Group team, boolean async) {
        super(async);
        this.team = team;
    }

    public Group clan() {
        return team;
    }

}
