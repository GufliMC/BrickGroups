package com.guflimc.brick.groups.spigot.api.events;

import com.guflimc.brick.groups.api.domain.Group;
import com.guflimc.brick.groups.api.domain.Profile;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ProfileClanInviteRejectEvent extends ProfileClanEvent {

    public ProfileClanInviteRejectEvent(Group team, Profile profile, boolean async) {
        super(team, profile, async);
    }

    //

    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
