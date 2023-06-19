package com.guflimc.brick.groups.api.domain;

import com.guflimc.brick.orm.api.attributes.AttributeKey;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface Group {

    UUID id();

    String name();

    GroupType type();

    int memberCount();

    Instant createdAt();

    // attributes

    <T> void setAttribute(AttributeKey<T> key, T value);

    <T> void removeAttribute(AttributeKey<T> key);

    <T> Optional<T> attribute(AttributeKey<T> key);

    // trailts

    <T extends GroupTrait> Optional<T> trait(Class<T> trait);

}
