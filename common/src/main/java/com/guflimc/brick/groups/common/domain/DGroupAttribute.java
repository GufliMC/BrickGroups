package com.guflimc.brick.groups.common.domain;

import com.guflimc.brick.orm.api.attributes.AttributeKey;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbForeignKey;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(
        name = "group_attributes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "name"})
)
public class DGroupAttribute extends DAttribute {

    @ManyToOne(optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    private DGroup group;

    public <T> DGroupAttribute(@NotNull DGroup group, @NotNull AttributeKey<T> key, @NotNull T value) {
        super(key, value);
        this.group = group;
    }

}
