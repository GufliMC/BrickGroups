package com.guflimc.brick.groups.spigot.api.events;

import com.guflimc.brick.groups.api.domain.Group;
import com.guflimc.brick.groups.api.domain.Profile;

public abstract class ProfileClanEvent extends ClanEvent {

    private final Profile profile;

    public ProfileClanEvent(Group team, Profile profile, boolean async) {
        super(team, async);
        this.profile = profile;
    }

    public Profile profile() {
        return profile;
    }

}
