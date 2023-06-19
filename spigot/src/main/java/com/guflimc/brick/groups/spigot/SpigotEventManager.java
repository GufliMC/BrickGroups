package com.guflimc.brick.groups.spigot;

import com.guflimc.brick.groups.api.domain.Group;
import com.guflimc.brick.groups.api.domain.Profile;
import com.guflimc.brick.groups.common.EventManager;
import com.guflimc.brick.groups.spigot.api.events.*;
import com.guflimc.clans.spigot.api.events.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import java.util.function.Supplier;

public class SpigotEventManager extends EventManager {

    private void wrap(Supplier<Event> supplier) {
        try {
            Bukkit.getServer().getPluginManager().callEvent(supplier.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Group team) {
        wrap(() -> new ClanCreateEvent(team, !Bukkit.isPrimaryThread()));
    }

    @Override
    public void onDelete(Group team) {
        wrap(() -> new ClanDeleteEvent(team, !Bukkit.isPrimaryThread()));
    }

    @Override
    public void onJoin(Profile profile, Group team) {
        wrap(() -> new ProfileClanJoinEvent(team, profile, !Bukkit.isPrimaryThread()));
    }

    @Override
    public void onLeave(Profile profile, Group team) {
        wrap(() -> new ProfileClanLeaveEvent(team, profile, !Bukkit.isPrimaryThread()));
    }

    @Override
    public void onInvite(Profile profile, Group team) {
        wrap(() -> new ProfileClanInviteEvent(team, profile, !Bukkit.isPrimaryThread()));
    }

    @Override
    public void onInviteCancel(Profile profile, Group team) {
        wrap(() -> new ProfileClanInviteDeleteEvent(team, profile, !Bukkit.isPrimaryThread()));
    }

    @Override
    public void onInviteDecline(Profile profile, Group team) {
        wrap(() -> new ProfileClanInviteRejectEvent(team, profile, !Bukkit.isPrimaryThread()));
    }

}
