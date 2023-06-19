package com.guflimc.brick.groups.api.domain.type;

import com.guflimc.brick.groups.api.domain.Group;
import com.guflimc.brick.groups.api.domain.GroupTrait;
import com.guflimc.brick.orm.api.attributes.AttributeKey;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class GroupColorTrait extends AbstractGroupTrait {

    private final static AttributeKey<Integer> GROUP_COLOR = new AttributeKey<>("BRICK_GROUP_COLOR", Integer.class,
            String::valueOf, Integer::parseInt);

    protected GroupColorTrait(@NotNull Group group) {
        super(group);
    }

    public int rgbColor() {
        return group.attribute(GROUP_COLOR).orElse(Color.WHITE.getRGB());
    }

    public void setRgbColor(int rgb) {
        group.setAttribute(GROUP_COLOR, rgb);
    }

    public TextColor textColor() {
        return TextColor.color(rgbColor());
    }

    public NamedTextColor namedTextColor() {
        return NamedTextColor.nearestTo(textColor());
    }

}
