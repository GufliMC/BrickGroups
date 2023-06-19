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
        name = "profile_attributes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"profile_id", "name"})
)
public class DProfileAttribute extends DAttribute {

    @ManyToOne(optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    private DProfile profile;

    public <T> DProfileAttribute(@NotNull DProfile profile, @NotNull AttributeKey<T> key, @NotNull T value) {
        super(key, value);
        this.profile = profile;
    }

}
