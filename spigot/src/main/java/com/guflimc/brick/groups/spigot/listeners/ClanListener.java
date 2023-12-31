package com.guflimc.brick.groups.spigot.listeners;

import com.guflimc.brick.groups.spigot.api.SpigotClanAPI;
import com.guflimc.brick.groups.spigot.api.events.ProfileClanJoinEvent;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ClanListener implements Listener {

    @EventHandler
    public void onClanJoin(ProfileClanJoinEvent event) {
        SpigotClanAPI.get().onlinePlayers(event.clan()).forEach(p -> {
            SpigotI18nAPI.get(this).send(p, "events.clan.join", event.profile().name());
        });
    }

}
