package com.guflimc.brick.groups.common.domain;

import com.guflimc.brick.groups.api.domain.Profile;
import com.guflimc.brick.groups.api.domain.Group;
import com.guflimc.brick.groups.api.domain.type.GroupInviteTrait;
import com.guflimc.brick.groups.common.EventManager;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbDefault;
import io.ebean.annotation.DbForeignKey;
import io.ebean.annotation.WhenCreated;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "group_invites")
public class DGroupInvite implements GroupInviteTrait.GroupInvite {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(targetEntity = DGroup.class, optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    private DGroup group;

    @ManyToOne(targetEntity = DProfile.class, optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    private DProfile sender;

    @ManyToOne(targetEntity = DProfile.class, optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    private DProfile target;

    @DbDefault("false")
    private boolean declined;

    @DbDefault("false")
    private boolean accepted;

    @DbDefault("false")
    private boolean cancelled;

    @WhenCreated
    private Instant createdAt = Instant.now();

    //

    public DGroupInvite() {
    }

    public DGroupInvite(@NotNull DGroup group, @NotNull DProfile sender, @NotNull DProfile target) {
        this.group = group;
        this.sender = sender;
        this.target = target;

        EventManager.INSTANCE.onInviteSent(this);
    }

    @Override
    public Profile sender() {
        return sender;
    }

    @Override
    public Profile target() {
        return target;
    }

    @Override
    public Group group() {
        return group;
    }

    @Override
    public void decline() {
        if (isAnswered()) {
            throw new IllegalStateException("This invite is already answered.");
        }

        this.declined = true;
        EventManager.INSTANCE.onInviteDecline(this);
    }

    @Override
    public void accept() {
        if (isAnswered()) {
            throw new IllegalStateException("This invite is already answered.");
        }

        this.accepted = true;
        target.join(group);
    }

    @Override
    public void cancel() {
        if (isAnswered()) {
            throw new IllegalStateException("This invite is already answered.");
        }

        this.cancelled = true;
        EventManager.INSTANCE.onInviteCancel(this);
    }

    @Override
    public boolean isExpired() {
        return Instant.now().isAfter(createdAt.plus(24, ChronoUnit.HOURS));
    }

    @Override
    public boolean isDeclined() {
        return declined;
    }

    @Override
    public boolean isAccepted() {
        return accepted;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    Instant createdAt() {
        return createdAt;
    }

}