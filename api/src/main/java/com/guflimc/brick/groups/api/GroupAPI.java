package com.guflimc.brick.groups.api;

import org.jetbrains.annotations.ApiStatus;

public class GroupAPI {

    private GroupAPI() {}

    private static GroupManager groupManager;

    @ApiStatus.Internal
    public static void register(GroupManager manager) {
        groupManager = manager;
    }

    //

    public static GroupManager get() {
        return groupManager;
    }
    
}
