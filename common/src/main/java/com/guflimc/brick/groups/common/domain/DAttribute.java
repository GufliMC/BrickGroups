package com.guflimc.brick.groups.common.domain;

import com.guflimc.brick.orm.api.attributes.AttributeKey;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@MappedSuperclass
public abstract class DAttribute {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "attrvalue", nullable = false)
    private String value;

    public <T> DAttribute(@NotNull AttributeKey<T> key, @NotNull T value) {
        this.name = key.name();
        this.value = key.serialize(value);
    }

    public String name() {
        return name;
    }

    public <T> void setValue(AttributeKey<T> key, T value) {
        if (value == null) {
            this.value = null;
            return;
        }

        this.value = key.serialize(value);
    }

    public <T> T value(AttributeKey<T> key) {
        return key.deserialize(value);
    }

}
