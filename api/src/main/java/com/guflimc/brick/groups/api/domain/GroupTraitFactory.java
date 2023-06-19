package com.guflimc.brick.groups.api.domain;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface GroupTraitFactory<T extends GroupTrait> {

    T create(@NotNull Group group);

}
