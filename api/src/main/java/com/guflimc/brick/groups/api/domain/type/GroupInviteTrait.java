package com.guflimc.brick.groups.api.domain.type;

import com.guflimc.brick.groups.api.domain.Profile;
import com.guflimc.brick.groups.api.domain.Group;
import com.guflimc.brick.groups.api.domain.GroupTrait;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public interface GroupInviteTrait extends GroupTrait {

    void invite(@NotNull Profile sender, @NotNull Profile target);

    Collection<GroupInvite> invites();

    Optional<GroupInvite> invite(@NotNull Profile target);

    //

    interface GroupInvite {

        Profile sender();

        Profile target();

        Group group();

        //

        void decline();

        void accept();

        void cancel();

        //

        boolean isExpired();

        boolean isDeclined();

        boolean isAccepted();

        boolean isCancelled();

        default boolean isAnswered() {
            return isDeclined() || isAccepted();
        }

        default boolean isActive() {
            return !isAnswered() && !isExpired() && !isCancelled();
        }

    }

}
