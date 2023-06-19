package com.guflimc.brick.groups.api.domain;

import com.guflimc.brick.orm.api.attributes.AttributeKey;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface Profile {

    UUID id();

    String name();

    Collection<Membership> memberships();

    Collection<Membership> memberships(@NotNull GroupType groupType);

    Optional<Membership> membership(@NotNull Group group);

    Instant createdAt();

    // actions

    void join(@NotNull Group group);

    // attributes

    <T> void setAttribute(AttributeKey<T> key, T value);

    <T> void removeAttribute(AttributeKey<T> key);

    <T> Optional<T> attribute(AttributeKey<T> key);


}
