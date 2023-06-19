package com.guflimc.brick.groups.api.domain.type;

import com.guflimc.brick.groups.api.domain.Group;
import com.guflimc.brick.groups.api.domain.GroupTrait;
import com.guflimc.brick.groups.api.domain.GroupTraitFactory;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractGroupTrait implements GroupTrait {

    protected final Group group;

    protected AbstractGroupTrait(@NotNull Group group) {
        this.group = group;
    }
}
