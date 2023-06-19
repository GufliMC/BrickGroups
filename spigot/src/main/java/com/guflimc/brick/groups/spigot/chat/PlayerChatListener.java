package com.guflimc.brick.groups.spigot.chat;

import com.guflimc.brick.chat.spigot.api.event.SpigotPlayerChannelChatEvent;
import com.guflimc.brick.groups.api.GroupAPI;
import com.guflimc.brick.groups.api.domain.Group;
import com.guflimc.brick.groups.api.domain.Profile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void onChat(SpigotPlayerChannelChatEvent event) {
        if ( !(event.chatChannel() instanceof ClanChatChannel) ) {
            return;
        }

        Group team = GroupAPI.get().findCachedProfile(event.player().getUniqueId())
                .flatMap(Profile::clanProfile).orElseThrow().clan();

        event.recipients().removeIf(p ->
                !GroupAPI.get().findCachedProfile(p.getUniqueId())
                .flatMap(Profile::clanProfile).orElseThrow()
                .clan().equals(team));
    }

}
