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
        name = "membership_attributes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"membership_id", "name"})
)
public class DMembershipAttribute extends DAttribute {

    @ManyToOne(optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    private DMembership membership;

    public <T> DMembershipAttribute(@NotNull DMembership membership, @NotNull AttributeKey<T> key, @NotNull T value) {
        super(key, value);
        this.membership = membership;
    }

}
