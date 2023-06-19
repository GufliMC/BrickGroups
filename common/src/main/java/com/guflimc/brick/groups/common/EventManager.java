package com.guflimc.brick.groups.common;

import com.guflimc.brick.groups.api.domain.Group;
import com.guflimc.brick.groups.api.domain.Profile;
import com.guflimc.brick.groups.api.domain.type.GroupInviteTrait;

public abstract class EventManager {

    public static EventManager INSTANCE;

    //

    public abstract void onCreate(Group team);

    public abstract void onDelete(Group team);

    public abstract void onJoin(Profile profile, Group team);

    public abstract void onLeave(Profile profile, Group team);

    public abstract void onInviteSent(GroupInviteTrait.GroupInvite invite);

    public abstract void onInviteCancel(GroupInviteTrait.GroupInvite invite);

    public abstract void onInviteDecline(GroupInviteTrait.GroupInvite invite);

}
