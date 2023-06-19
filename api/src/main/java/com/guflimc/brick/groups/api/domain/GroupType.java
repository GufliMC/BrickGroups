package com.guflimc.brick.groups.api.domain;

import com.guflimc.brick.orm.api.attributes.AttributeKey;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GroupType {

    private final String name;
    private final Map<AttributeKey<?>, Object> attributes;
    private final Map<Class<? extends GroupTrait>, GroupTraitFactory<?>> traits;

    private GroupType(@NotNull String name,
                        @NotNull Map<AttributeKey<?>, Object> attributes,
                        @NotNull Map<Class<? extends GroupTrait>, GroupTraitFactory<?>> traits) {
        this.name = name;
        this.attributes = Map.copyOf(attributes);
        this.traits = Map.copyOf(traits);
    }

    //

    public final String name() {
        return name;
    }

    public final Collection<AttributeKey<?>> attributes() {
        return attributes.keySet();
    }

    public final <T> Optional<T> attribute(@NotNull AttributeKey<T> key) {
        return Optional.ofNullable(key.type().cast(attributes.get(key)));
    }

    public final Collection<Class<? extends GroupTrait>> traits() {
        return traits.keySet();
    }

    public final <T extends GroupTrait> Optional<T> trait(@NotNull Class<T> traitClass, @NotNull Group group) {
        return Optional.ofNullable((GroupTraitFactory<T>) traits.get(traitClass))
                .map(factory -> factory.create(group));
    }

    //

    @Override
    public final String toString() {
        return name;
    }

    //

    public static Builder builder(@NotNull String name) {
        if ( name.length() < 2 || !name.matches("^[a-z]+$") ) {
            throw new IllegalArgumentException("name must be at least 2 characters long and only contain lowercase letters");
        }
        return new Builder(name);
    }

    //

    public static class Builder {

        private final String name;
        private final Map<AttributeKey<?>, Object> attributes = new HashMap<>();
        private final Map<Class<? extends GroupTrait>, GroupTraitFactory<?>> traits = new HashMap<>();

        private Builder(String name) {
            this.name = name;
        }

        public <T> Builder withAttribute(@NotNull AttributeKey<T> key, @NotNull T value) {
            attributes.put(key, value);
            return this;
        }

        public <T extends GroupTrait> Builder withTrait(@NotNull Class<T> type, @NotNull GroupTraitFactory<T> factory) {
            traits.put(type, factory);
            return this;
        }

        public GroupType build() {
            return new GroupType(name, attributes, traits);
        }
    }


}
