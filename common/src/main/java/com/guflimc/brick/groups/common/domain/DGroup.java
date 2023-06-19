package com.guflimc.brick.groups.common.domain;

import com.guflimc.brick.groups.api.domain.Group;
import com.guflimc.brick.groups.api.domain.GroupTrait;
import com.guflimc.brick.groups.api.domain.GroupType;
import com.guflimc.brick.orm.api.attributes.AttributeKey;
import io.ebean.annotation.Formula;
import io.ebean.annotation.Index;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Entity
@Table(name = "groups")
@Index(columnNames = {"type", "name"}, unique = true)
public class DGroup implements Group {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private GroupType type;

    @Column(nullable = false)
    private String name;

    @Formula(select = "aggr.member_count",
            join = "join (select mb.group_id, count(mb.group_id) as member_count from memberships mb where mb.active = 1 group by mb.group_id) as aggr ON ${ta}.id = aggr.group_id")
    private int memberCount;

    @OneToMany(targetEntity = DGroupAttribute.class, mappedBy = "group",
            cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DGroupAttribute> attributes = new ArrayList<>();

    @OneToMany(targetEntity = DGroupInvite.class, mappedBy = "group",
            cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DGroupInvite> invites = new ArrayList<>();

    @WhenCreated
    private Instant createdAt;

    @WhenModified
    private Instant updatedAt;

    //

    private transient final Map<Class<?>, GroupTrait> traits = new ConcurrentHashMap<>();

    //

    public DGroup(@NotNull GroupType type, @NotNull String name) {
        this.type = type;
        this.name = name;
        onLoad();
    }

    @PostLoad
    public void onLoad() {
        type.traits().forEach(cls -> type.trait(cls, this).ifPresent(t -> traits.put(cls, t)));
    }

    //

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public GroupType type() {
        return type;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int memberCount() {
        return memberCount;
    }

    @Override
    public Instant createdAt() {
        return createdAt;
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
            attributes.add(new DGroupAttribute(this, key, value));
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

    // traits

    @Override
    public <T extends GroupTrait> Optional<T> trait(Class<T> type) {
        if ( traits.containsKey(type) ) {
            return Optional.of(type.cast(traits.get(type)));
        }

        for ( Class<?> cls : traits.keySet() ) {
            if ( type.isAssignableFrom(cls) ) {
                return Optional.of(type.cast(traits.get(cls)));
            }
        }

        return Optional.empty();
    }

    // INTERNAL

    public Collection<DGroupInvite> invites() {
        return Collections.unmodifiableCollection(invites);
    }

    public void addInvite(@NotNull DProfile sender, @NotNull DProfile target) {
        invites.add(new DGroupInvite(this, sender, target));
    }

    public void removeInvite(@NotNull DGroupInvite invite) {
        invites.remove(invite);
    }

    public Optional<DGroupInvite> invite(@NotNull DProfile target) {
        return invites.stream()
                .filter(invite -> invite.target().equals(target))
                .max(Comparator.comparing(DGroupInvite::createdAt));
    }

    void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

}