package com.guflimc.brick.groups.spigot.api;

import com.guflimc.brick.groups.api.GroupAPI;
import com.guflimc.brick.groups.api.GroupManager;
import com.guflimc.brick.groups.api.domain.Group;
import com.guflimc.brick.groups.api.domain.Membership;
import com.guflimc.brick.groups.api.domain.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public interface SpigotClanManager extends GroupManager {

    default Collection<Player> onlinePlayers(@NotNull Group team) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(p -> GroupAPI.get().findCachedProfile(p.getUniqueId())
                        .flatMap(Profile::clanProfile)
                        .map(Membership::clan)
                        .filter(c -> c.equals(team))
                        .isPresent()
                ).map(Player.class::cast).toList();
    }

    default Optional<Group> findClan(@NotNull Player player) {
        return GroupAPI.get()
                .findCachedProfile(player.getUniqueId())
                .flatMap(Profile::clanProfile)
                .map(Membership::clan);
    }

    default Optional<Membership> clanProfile(@NotNull Player player) {
        return GroupAPI.get().findCachedProfile(player.getUniqueId())
                .flatMap(Profile::clanProfile);
    }

    ItemStack crest(@NotNull Group team);

}
