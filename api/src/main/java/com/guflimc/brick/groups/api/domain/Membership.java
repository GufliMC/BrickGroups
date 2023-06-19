package com.guflimc.brick.groups.api.domain;

import com.guflimc.brick.orm.api.attributes.AttributeKey;

import java.time.Instant;
import java.util.Optional;

/**
 * Junction between Profile and Group. Represents a Profile's membership in a Group.
 */
public interface Membership {

    Profile profile();

    Group group();

    Instant createdAt();

    // actions

    void quit();

    //

    // attributes

    <T> void setAttribute(AttributeKey<T> key, T value);

    <T> void removeAttribute(AttributeKey<T> key);

    <T> Optional<T> attribute(AttributeKey<T> key);

}
