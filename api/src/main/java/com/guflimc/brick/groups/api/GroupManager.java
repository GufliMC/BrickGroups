package com.guflimc.brick.groups.api;

import com.guflimc.brick.groups.api.domain.Group;
import com.guflimc.brick.groups.api.domain.GroupType;
import com.guflimc.brick.groups.api.domain.Membership;
import com.guflimc.brick.groups.api.domain.Profile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface GroupManager {

    void reload();

    // groups

    Collection<Group> groups(@NotNull GroupType type);

    Optional<Group> group(@NotNull GroupType type, @NotNull String name);

    Optional<Group> group(@NotNull GroupType type, @NotNull UUID id);

    CompletableFuture<Group> create(@NotNull GroupType type, @NotNull String name);

    CompletableFuture<Void> persist(@NotNull Group group);

    CompletableFuture<Void> remove(@NotNull Group group);

    // profiles

    CompletableFuture<Profile> profile(@NotNull UUID id);

    CompletableFuture<Profile> profile(@NotNull String name);

    CompletableFuture<List<Profile>> profiles(@NotNull Group group);

    CompletableFuture<Void> persist(@NotNull Profile profile);

}
