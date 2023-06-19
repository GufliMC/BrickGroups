package com.guflimc.brick.groups.common.domain;

import com.guflimc.brick.groups.api.GroupAPI;
import com.guflimc.brick.groups.api.domain.Group;
import com.guflimc.brick.groups.api.domain.Membership;
import com.guflimc.brick.groups.api.domain.Profile;
import com.guflimc.brick.groups.common.EventManager;
import com.guflimc.brick.orm.api.attributes.AttributeKey;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.*;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "memberships")
public class DMembership implements Membership {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(targetEntity = DProfile.class, optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    private DProfile profile;

    @ManyToOne(targetEntity = DGroup.class, optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    private DGroup group;

    @OneToMany(targetEntity = DMembershipAttribute.class, mappedBy = "membership",
            cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DMembershipAttribute> attributes = new ArrayList<>();

    @DbDefault("true")
    private boolean active = true;

    @WhenCreated
    private Instant createdAt;

    @WhenModified
    private Instant updatedAt;

    //

    public DMembership(@NotNull DProfile profile, @NotNull DGroup group) {
        this.profile = profile;
        this.group = group;
    }

    @Override
    public Profile profile() {
        return profile;
    }

    @Override
    public Group group() {
        return group;
    }

    @Override
    public Instant createdAt() {
        return createdAt;
    }

    // actions

    @Override
    public void quit() {
        group.setMemberCount(group.memberCount() - 1);

        this.active = false;
        profile.removeMembership(this);
        group.invites().removeIf(invite -> invite.target().equals(profile) || invite.sender().equals(profile));

        GroupAPI.get().persist(profile);
        GroupAPI.get().persist(group);

        EventManager.INSTANCE.onLeave(profile, group);
    }

    @Override
    public <T> void setAttribute(AttributeKey<T> key, T value) {
        if (value == null) {
            removeAttribute(key);
            return;
        }

        DAttribute attribute = attributes.stream()
                .filter(attr -> attr.name().equals(key.name()))
                .findFirst().orElse(null);

        if (attribute == null) {
            attributes.add(new DMembershipAttribute(this, key, value));
            return;
        }

        attribute.setValue(key, value);
    }

    @Override
    public <T> void removeAttribute(AttributeKey<T> key) {
        attributes.removeIf(attr -> attr.name().equals(key.name()));
    }

    @Override
    public <T> Optional<T> attribute(AttributeKey<T> key) {
        return attributes.stream().filter(attr -> attr.name().equals(key.name()))
                .findFirst().map(ra -> ra.value(key));
    }

}