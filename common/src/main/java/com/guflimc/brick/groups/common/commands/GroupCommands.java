package com.guflimc.brick.groups.common.commands;

import cloud.commandframework.annotations.Argument;
import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.colonel.common.Colonel;
import com.guflimc.brick.groups.api.GroupAPI;
import com.guflimc.brick.groups.api.domain.Profile;
import com.guflimc.brick.groups.api.domain.Group;
import com.guflimc.brick.groups.api.domain.GroupType;
import com.guflimc.brick.groups.api.domain.type.GroupInviteTrait;
import com.guflimc.brick.groups.api.domain.type.GroupMemberLimitTrait;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import java.util.List;

//@CommandContainer
public class GroupCommands {

    private final AudienceProvider adventure;

    public GroupCommands(AudienceProvider adventure, List<GroupType> types, Colonel<Audience> colonel) {
        this.adventure = adventure;
        types.forEach(type -> register(type, colonel));
    }

    private void register(GroupType type, Colonel<Audience> colonel) {
        colonel.builder()
                .path("t " + type.name() + " list")
                .executor(ctx -> list(type, ctx.source()))
                .register();

        colonel.builder()
                .path("t " + type.name() + " invite")
                .parameter("username").type(String.class).completer(Audience.class).done()
                .source(Profile.class)
                .executor(ctx -> invite(type, ctx.source(), ctx.source(0), ctx.argument("username")))
                .register();
    }

    private void list(GroupType type, Audience sender) {
        I18nAPI.get(this).send(sender, "cmd.teams.list",
                type.name(), GroupAPI.get().teams(type).stream().map(Group::name).toList());
    }

    private void invite(GroupType type, Audience sender, Profile senderp, String username) {
        if ( senderp.membership(type).isEmpty() ) {
            I18nAPI.get(this).send(sender, "cmd.error.base.not.in.team");
            return;
        }

        Group team = senderp.membership(type).orElseThrow().team();

        GroupInviteTrait invTrait = team.trait(GroupInviteTrait.class).orElse(null);
        if ( invTrait == null ) {
            I18nAPI.get(this).send(sender, "cmd.error.base.no.permission");
            return;
        }

        // TODO invite permission with trait
//        if (!team.trait(TeamPermissionTrait.class)
//                .map(trait -> trait.hasPermission(senderp, "invite"))
//                .orElse(false)) {
//            I18nAPI.get(this).send(sender, "cmd.error.base.no.permission");
//            return;
//        }

        if ( team.trait(GroupMemberLimitTrait.class).map(trait -> trait.memberLimit() <= team.memberCount()).orElse(false) ) {
            I18nAPI.get(this).send(sender, "cmd.teams.invite.error.max.members");
            return;
        }

        GroupAPI.get().profile(username).thenAccept(target -> {
            if (target == null) {
                I18nAPI.get(this).send(sender, "cmd.error.args.player", username);
                return;
            }

            if (target.membership(type).isPresent()) {
                I18nAPI.get(this).send(sender, "cmd.teams.invite.error.already.in.team.type");
                return;
            }

            GroupInviteTrait.TeamInvite invite = invTrait.invite(target).orElse(null);
            if ( invite != null && !invite.isExpired() && !invite.isAnswered()) {
                I18nAPI.get(this).send(sender, "cmd.teams.invite.error.already.invited");
                return;
            }

            invTrait.invite(senderp, target);
            GroupAPI.get().update(team);

            // send messages
            I18nAPI.get(this).send(sender, "cmd.teams.invite.sender", target.name());

            Audience targetAudience = adventure.player(target.id());

            // to target
            Component accept = I18nAPI.get(this).hoverable(targetAudience, "chat.button.accept", "chat.button.accept.hover")
                    .clickEvent(ClickEvent.runCommand("/t " + type.name() + " join " + team.name()));
            Component decline = I18nAPI.get(this).hoverable(targetAudience, "chat.button.decline", "chat.button.decline.hover")
                    .clickEvent(ClickEvent.runCommand("/t " + type.name() + " reject " + team.name()));

            Component message = I18nAPI.get(this).translate(targetAudience, "cmd.teams.invite.target", senderp.name(), team.name());

            int width = I18nAPI.get(this).width(message);
            Component buttons = I18nAPI.get(this).paddingAround(width, accept, decline);

            I18nAPI.get(this).menu(targetAudience, message, Component.text(""), buttons);

        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    public void uninvite(GroupType type, Audience sender, Profile senderp, String username) {
        if ( senderp.membership(type).isEmpty() ) {
            I18nAPI.get(this).send(sender, "cmd.error.base.not.in.team");
            return;
        }

        Group team = senderp.membership(type).orElseThrow().team();

        GroupInviteTrait invTrait = team.trait(GroupInviteTrait.class).orElse(null);
        if ( invTrait == null ) {
            I18nAPI.get(this).send(sender, "cmd.error.base.no.permission");
            return;
        }

        // TODO invite permission with trait
//        if (!team.trait(TeamPermissionTrait.class)
//                .map(trait -> trait.hasPermission(senderp, "invite"))
//                .orElse(false)) {
//            I18nAPI.get(this).send(sender, "cmd.error.base.no.permission");
//            return;
//        }

        GroupAPI.get().profile(username).thenAccept(target -> {
            if (target == null) {
                I18nAPI.get(this).send(sender, "cmd.error.args.player", username);
                return;
            }

            GroupInviteTrait.TeamInvite invite = invTrait.invite(target).orElse(null);
            if ( invite == null || !invite.isActive() ) {
                I18nAPI.get(this).send(sender, "cmd.teams.uninvite.error.not.invited");
                return;
            }

            invite.cancel();
            GroupAPI.get().update(team);

            // send messages
            I18nAPI.get(this).send(sender, "cmd.teams.uninvite.sender", target.name());
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    public void kick(GroupType type, Audience sender, Profile senderp, String username) {
        if ( senderp.membership(type).isEmpty() ) {
            I18nAPI.get(this).send(sender, "cmd.error.base.not.in.team");
            return;
        }

        Group team = senderp.membership(type).orElseThrow().team();

        // TODO kick permission with trait
//        if (!team.trait(TeamPermissionTrait.class)
//                .map(trait -> trait.hasPermission(senderp, "kick"))
//                .orElse(false)) {
//            I18nAPI.get(this).send(sender, "cmd.error.base.no.permission");
//            return;
//        }

        GroupAPI.get().profile(username).thenAccept(target -> {
            if (target == null) {
                I18nAPI.get(this).send(sender, "cmd.error.args.player", username);
                return;
            }

            if ( target.membership(team).isEmpty() ) {
                I18nAPI.get(this).send(sender, "cmd.teams.kick.error.not.in.clan");
                return;
            }

            target.membership(team).get().quit();
            GroupAPI.get().persist(target);

            // send messages
            I18nAPI.get(this).send(sender, "cmd.teams.kick.sender", target.name());

            Audience targetAudience = adventure.player(target.id());
            I18nAPI.get(this).send(targetAudience, "cmd.teams.kick.target");
        });
    }

    public void join(GroupType type, Audience sender, Profile senderp, Group team) {
        if ( senderp.membership(type).isPresent()) {
            I18nAPI.get(this).send(sender, "cmd.error.base.already.in.team.type");
            return;
        }

        if ( team.trait(GroupMemberLimitTrait.class).map(trait -> trait.memberLimit() <= team.memberCount()).orElse(false) ) {
            I18nAPI.get(this).send(sender, "cmd.teams.join.error.max.members");
            return;
        }

        GroupInviteTrait invTrait = team.trait(GroupInviteTrait.class).orElse(null);
        if ( invTrait != null ) {
            GroupInviteTrait.TeamInvite invite = invTrait.invite(senderp).orElse(null);
            if ( invite == null || !invite.isActive() ) {
                I18nAPI.get(this).send(sender, "cmd.teamsjoin.error.missing");
                return;
            }

            invite.accept();
        } else {
            senderp.join(team);
        }

        GroupAPI.get().persist(senderp);

        I18nAPI.get(this).send(sender, "cmd.teams.join", team.name());
    }

    public void reject(GroupType type, Audience sender, Profile senderp, Group team) {
        GroupInviteTrait invTrait = team.trait(GroupInviteTrait.class).orElse(null);
        if ( invTrait == null ) {
            I18nAPI.get(this).send(sender, "cmd.error.base.no.permission");
            return;
        }

        GroupInviteTrait.TeamInvite invite = invTrait.invite(target).orElse(null);
        if ( invite == null || !invite.isActive() ) {
            I18nAPI.get(this).send(sender, "cmd.teams.join.error.missing");
            return;
        }

        invite.decline();
        GroupAPI.get().persist(senderp);

        I18nAPI.get(this).send(sender, "cmd.teams.decline", team.name());

        Audience invsender = adventure.player(invite.sender().id());
        I18nAPI.get(this).send(invsender, "cmd.teams.decline.sender", senderp.name());
    }

    public void quit(GroupType type, Audience sender, Profile senderp) {
        if ( senderp.membership(type).isEmpty() ) {
            I18nAPI.get(this).send(sender, "cmd.error.base.not.in.team");
            return;
        }

        senderp.membership(type).get().quit();
        GroupAPI.get().persist(senderp);

        I18nAPI.get(this).send(sender, "cmd.teams.quit", type);
    }

    public void disband(GroupType type, Audience sender, Profile senderp) {
        if ( senderp.membership(type).isEmpty() ) {
            I18nAPI.get(this).send(sender, "cmd.error.base.not.in.team");
            return;
        }

//        if (!sprofile.clanProfile().get().isLeader()) {
//            I18nAPI.get(this).send(sender, "cmd.clans.perms.error.not.leader");
//            return;
//        }

        GroupAPI.get().remove(senderp.membership(type).get().team());
        I18nAPI.get(this).send(sender, "cmd.team.disband");
    }

    public void create(Audience sender, Profile sprofile, @Argument("name") String name, @Argument("tag") String tag) {
        if (sprofile.clanProfile().isPresent()) {
            I18nAPI.get(this).send(sender, "cmd.error.base.already.in.clan");
            return;
        }

        if (sprofile.clanProfile().isPresent()) {
            I18nAPI.get(this).send(sender, "cmd.clans.join.error.already");
            return;
        }

        if (!name.matches("[a-zA-Z0-9]{2,24}")) {
            I18nAPI.get(this).send(sender, "cmd.clans.create.error.name.format", name);
            return;
        }

        if (!tag.matches("[a-zA-Z0-9]{2,3}")) {
            I18nAPI.get(this).send(sender, "cmd.clans.create.error.tag.format", tag);
            return;
        }

        if (GroupAPI.get().findClan(name).isPresent()) {
            I18nAPI.get(this).send(sender, "cmd.clans.create.error.name.exists");
            return;
        }

        tag = tag.toUpperCase();
        if (GroupAPI.get().findClanByTag(tag).isPresent()) {
            I18nAPI.get(this).send(sender, "cmd.clans.create.error.tag.exists");
            return;
        }

        GroupAPI.get().create(sprofile, name, tag).thenAccept(clan -> {
            I18nAPI.get(this).send(sender, "cmd.clans.create", clan.name(), clan.tag());
        });
    }

}
