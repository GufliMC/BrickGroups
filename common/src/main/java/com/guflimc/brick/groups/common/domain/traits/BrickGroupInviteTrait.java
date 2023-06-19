package com.guflimc.brick.groups.common.domain.traits;

import com.guflimc.brick.groups.api.domain.Profile;
import com.guflimc.brick.groups.api.domain.type.GroupInviteTrait;
import com.guflimc.brick.groups.common.domain.DGroup;
import com.guflimc.brick.groups.common.domain.DProfile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public class BrickGroupInviteTrait implements GroupInviteTrait {

    private final DGroup group;

    public BrickGroupInviteTrait(@NotNull DGroup group) {
        this.group = group;
    }

    @Override
    public void invite(@NotNull Profile sender, @NotNull Profile target) {
        group.addInvite((DProfile) sender, (DProfile) target);
    }

    @Override
    public Collection<GroupInvite> invites() {
        return group.invites().stream().map(inv -> (GroupInvite) inv).toList();
    }

    @Override
    public Optional<GroupInvite> invite(@NotNull Profile target) {
        return group.invite((DProfile) target).map(inv -> inv);
    }
}
