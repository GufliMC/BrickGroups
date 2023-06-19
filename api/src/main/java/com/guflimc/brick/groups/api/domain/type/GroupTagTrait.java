package com.guflimc.brick.groups.api.domain.type;

import com.guflimc.brick.groups.api.domain.Group;
import com.guflimc.brick.groups.api.domain.GroupTrait;
import com.guflimc.brick.orm.api.attributes.AttributeKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class GroupTagTrait extends AbstractGroupTrait {

    private final static AttributeKey<Component> GROUP_TAG = new AttributeKey<>("BRICK_GROUP_TAG", Component.class,
            GsonComponentSerializer.gson()::serialize, GsonComponentSerializer.gson()::deserialize);

    private final static AttributeKey<Integer> GROUP_TAG_MAX_LENGTH = new AttributeKey<>("BRICK_GROUP_TAG_MAX_LENGTH", Integer.class,
            String::valueOf, Integer::parseInt);
    
    public GroupTagTrait(@NotNull Group group) {
        super(group);
    }

    public int maxTagLength() {
        return group.attribute(GROUP_TAG_MAX_LENGTH).orElse(64);
    }

    public void setMaxTagLength(int maxTagLength) {
        group.setAttribute(GROUP_TAG_MAX_LENGTH, maxTagLength);
    }

    public Component tag() {
        return group.attribute(GROUP_TAG).orElse(Component.text(""));
    }

    public void setTag(Component tag) {
        String str = PlainTextComponentSerializer.plainText().serialize(tag);
        if ( str.length() < 2 || str.length() > maxTagLength()) {
            throw new IllegalArgumentException("Tag must be between 2 and " + maxTagLength() + " characters long.");
        }

        group.setAttribute(GROUP_TAG, tag);
    }

}
