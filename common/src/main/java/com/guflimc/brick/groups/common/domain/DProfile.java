package com.guflimc.brick.groups.common.domain;

import com.guflimc.brick.groups.api.domain.Group;
import com.guflimc.brick.groups.api.domain.GroupType;
import com.guflimc.brick.groups.api.domain.Membership;
import com.guflimc.brick.groups.api.domain.Profile;
import com.guflimc.brick.groups.common.EventManager;
import com.guflimc.brick.orm.api.attributes.AttributeKey;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.*;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "profiles")
public class DProfile implements Profile {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @OneToMany(targetEntity = DMembership.class, mappedBy = "profile", fetch = FetchType.EAGER,
            orphanRemoval = true, cascade = CascadeType.ALL)
    @Where(clause = "active = 1")
    @DbForeignKey(onDelete = ConstraintMode.SET_NULL)
    private List<DMembership> memberships;

    @OneToMany(targetEntity = DProfileAttribute.class, mappedBy = "profile",
            cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DProfileAttribute> attributes = new ArrayList<>();

    private Instant lastSeenAt;

    @WhenCreated
    private Instant createdAt;

    @WhenModified
    private Instant updatedAt;

    //

    private DProfile() {
    }

    public DProfile(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Collection<Membership> memberships() {
        return Collections.unmodifiableCollection(memberships);
    }

    @Override
    public Collection<Membership> memberships(@NotNull GroupType type) {
        return memberships.stream()
                .filter(m -> m.group().type().equals(type))
                .map(m -> (Membership) m)
                .toList();
    }

    @Override
    public Optional<Membership> membership(@NotNull Group team) {
        return memberships.stream()
                .filter(m -> m.group().equals(team))
                .map(m -> (Membership) m)
                .findFirst();
    }

    @Override
    public Instant createdAt() {
        return createdAt;
    }

    // actions

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void join(@NotNull Group group) {
        if (membership(group).isPresent()) {
            throw new IllegalArgumentException("Already a member of this group.");
        }

        // join new group
        DMembership membership = new DMembership(this, (DGroup) group);
        memberships.add(membership);

        ((DGroup) group).setMemberCount(group.memberCount() + 1);

        EventManager.INSTANCE.onJoin(this, group);
    }

    // attributes

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
            attributes.add(new DProfileAttribute(this, key, value));
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

    // INTERNAL

    public void removeMembership(DMembership membership) {
        memberships.remove(membership);
    }

}