package com.guflimc.brick.groups.api.domain.type;

import com.guflimc.brick.groups.api.domain.Group;
import com.guflimc.brick.orm.api.attributes.AttributeKey;
import org.jetbrains.annotations.NotNull;

public class GroupMemberLimitTrait extends AbstractGroupTrait {

    private final static AttributeKey<Integer> GROUP_MEMBER_LIMIT = new AttributeKey<>("BRICK_GROUP_MEMBER_LIMIT", Integer.class,
            String::valueOf, Integer::parseInt);

    protected GroupMemberLimitTrait(@NotNull Group group) {
        super(group);
    }

    public int memberLimit() {
        return group.attribute(GROUP_MEMBER_LIMIT).orElse(0);
    }

    void setMemberLimit(int limit) {
        if ( limit < 0 ) {
            throw new IllegalArgumentException("limit must be positive");
        }
        group.setAttribute(GROUP_MEMBER_LIMIT, limit);
    }

}
