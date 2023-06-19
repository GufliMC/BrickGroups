package com.guflimc.brick.groups.spigot.api;

import com.guflimc.brick.groups.api.GroupAPI;
import org.jetbrains.annotations.ApiStatus;

public class SpigotClanAPI {

    private SpigotClanAPI() {
    }

    private static SpigotClanManager clanManager;

    @ApiStatus.Internal
    public static void register(SpigotClanManager manager) {
        clanManager = manager;
        GroupAPI.register(manager);
    }

    //

    public static SpigotClanManager get() {
        return clanManager;
    }

}
