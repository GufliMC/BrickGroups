package com.guflimc.brick.groups.common;

import com.guflimc.brick.groups.api.GroupManager;
import com.guflimc.brick.groups.api.domain.Group;
import com.guflimc.brick.groups.api.domain.GroupType;
import com.guflimc.brick.groups.api.domain.Profile;
import com.guflimc.brick.groups.common.domain.DGroup;
import com.guflimc.brick.groups.common.domain.DProfile;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class BaseGroupManager implements GroupManager {

    private final Logger logger = LoggerFactory.getLogger(BaseGroupManager.class);

    protected final GroupConfig config;
    protected final GroupDatabaseContext databaseContext;

    private final Set<DGroup> groups = new CopyOnWriteArraySet<>();
    private final Set<DProfile> profiles = new CopyOnWriteArraySet<>();

    public BaseGroupManager(GroupConfig config, GroupDatabaseContext databaseContext) {
        this.config = config;
        this.databaseContext = databaseContext;
        reload();
    }

    @Override
    public void reload() {
        logger.info("Reloading group manager...");

        groups.clear();
        groups.addAll(databaseContext.findAllAsync(DGroup.class).join());
    }

    //

    @Override
    public Collection<Group> groups(@NotNull GroupType type) {
        return groups.stream()
                .filter(c -> c.type() == type)
                .map(t -> (Group) t)
                .toList();
    }

    @Override
    public Optional<Group> group(@NotNull GroupType type, @NotNull String name) {
        return groups.stream()
                .filter(c -> c.type() == type)
                .filter(c -> c.name().equalsIgnoreCase(name))
                .findFirst().map(c -> c);
    }

    @Override
    public Optional<Group> group(@NotNull GroupType type, @NotNull UUID id) {
        return groups.stream()
                .filter(c -> c.type() == type)
                .filter(c -> c.id().equals(id))
                .findFirst().map(c -> c);
    }

    @Override
    public CompletableFuture<Group> create(@NotNull GroupType type, @NotNull String name) {
        if (groups(type).stream().anyMatch(c -> c.name().equalsIgnoreCase(name))) {
            throw new IllegalArgumentException("A group of that type with that name already exists.");
        }

        DGroup group = new DGroup(type, name);
        groups.add(group);

        return databaseContext.persistAsync(group).thenApply(n -> {
            EventManager.INSTANCE.onCreate(group);
            return group;
        });
    }

    @Override
    public CompletableFuture<Profile> profile(@NotNull UUID id) {
        Profile profile = profiles.stream()
                .filter(p -> p.id().equals(id))
                .findFirst().orElse(null);
        if (profile != null) {
            return CompletableFuture.completedFuture(profile);
        }
        return databaseContext.findAsync(DProfile.class, id)
                .thenApply(p -> p);
    }

    @Override
    public CompletableFuture<Profile> profile(@NotNull String name) {
        Profile profile = profiles.stream()
                .filter(p -> p.name().equalsIgnoreCase(name))
                .findFirst().orElse(null);
        if (profile != null) {
            return CompletableFuture.completedFuture(profile);
        }
        return databaseContext.findAllWhereAsync(DProfile.class, "name", name)
                .thenApply(p -> p.isEmpty() ? null : p.get(0));
    }

    @Override
    public CompletableFuture<Void> remove(@NotNull Group group) {
        groups.remove((DGroup) group);

        Set<CompletableFuture<?>> futures = new HashSet<>();

        // online players will leave group
        profiles.stream().map(p -> p.membership(group).orElse(null))
                .filter(Objects::nonNull)
                .forEach(membership -> {
                    membership.quit();
                    futures.add(persist(membership.profile()));
                });

        return databaseContext.removeAsync(group).thenCompose(n ->
                        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)))
                .thenRun(() -> EventManager.INSTANCE.onDelete(group));
    }

    @Override
    public CompletableFuture<Void> persist(@NotNull Group group) {
        return databaseContext.persistAsync(group);
    }

    @Override
    public CompletableFuture<Void> persist(@NotNull Profile profile) {
        return databaseContext.persistAsync(profile);
    }

    // edit profiles

    public CompletableFuture<Profile> login(@NotNull UUID id, @NotNull String name) {
        return profile(id).thenApply(p -> {
            // update name change
            ((DProfile) p).setName(name);
            databaseContext.persistAsync(p);
            return p;
        }).exceptionally(ex -> {
            // not found, create new profile
            DProfile profile = new DProfile(id, name);
            databaseContext.persistAsync(profile);
            return profile;
        }).thenApply(p -> {
            // add to cache
            DProfile dp = (DProfile) p;
            profiles.add(dp);
            return p;
        });
    }

    public void logout(@NotNull UUID id) {
        profiles.removeIf(p -> p.id().equals(id));
    }
}
