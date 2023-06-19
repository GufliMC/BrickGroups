-- apply changes
create table clans (
  id                            uuid not null,
  name                          varchar(255) not null,
  tag                           varchar(255) not null,
  rgb_color                     integer default 16581375 not null,
  max_members                   integer default 10 not null,
  crest_template_id             uuid,
  crest_config                  varchar(8192),
  created_at                    timestamp not null,
  updated_at                    timestamp not null,
  constraint uq_clans_name unique (name),
  constraint uq_clans_tag unique (tag),
  constraint pk_clans primary key (id)
);

create table clan_attributes (
  id                            uuid not null,
  name                          varchar(255) not null,
  attrvalue                     varchar(255) not null,
  clan_id                       uuid not null,
  constraint uq_clan_attributes_clan_id_name unique (clan_id,name),
  constraint pk_clan_attributes primary key (id)
);

create table clan_invites (
  id                            uuid not null,
  sender_id                     uuid not null,
  target_id                     uuid not null,
  clan_id                       uuid not null,
  rejected                      boolean default false not null,
  accepted                      boolean default false not null,
  cancelled                     boolean default false not null,
  created_at                    timestamp not null,
  constraint pk_clan_invites primary key (id)
);

create table clan_profiles (
  id                            uuid not null,
  profile_id                    uuid not null,
  clan_id                       uuid not null,
  leader                        boolean default false not null,
  active                        boolean default true not null,
  power                         float default 0 not null,
  created_at                    timestamp not null,
  updated_at                    timestamp not null,
  constraint pk_clan_profiles primary key (id)
);

create table clan_profile_permissions (
  id                            uuid not null,
  clan_profile_id               uuid not null,
  permkey                       varchar(255),
  created_at                    timestamp not null,
  updated_at                    timestamp not null,
  constraint pk_clan_profile_permissions primary key (id)
);

create table crest_templates (
  id                            uuid not null,
  name                          varchar(255) not null,
  type                          varchar(8192) not null,
  restricted                    boolean default false not null,
  constraint uq_crest_templates_name unique (name),
  constraint pk_crest_templates primary key (id)
);

create table profiles (
  id                            uuid not null,
  name                          varchar(255) not null,
  clan_profile_id               uuid,
  last_seen_at                  timestamp,
  created_at                    timestamp not null,
  updated_at                    timestamp not null,
  constraint uq_profiles_clan_profile_id unique (clan_profile_id),
  constraint pk_profiles primary key (id)
);

create table profile_attributes (
  id                            uuid not null,
  name                          varchar(255) not null,
  attrvalue                     varchar(255) not null,
  profile_id                    uuid not null,
  constraint uq_profile_attributes_profile_id_name unique (profile_id,name),
  constraint pk_profile_attributes primary key (id)
);

-- foreign keys and indices
create index ix_clans_crest_template_id on clans (crest_template_id);
alter table clans add constraint fk_clans_crest_template_id foreign key (crest_template_id) references crest_templates (id) on delete set null on update restrict;

create index ix_clan_attributes_clan_id on clan_attributes (clan_id);
alter table clan_attributes add constraint fk_clan_attributes_clan_id foreign key (clan_id) references clans (id) on delete cascade on update restrict;

create index ix_clan_invites_sender_id on clan_invites (sender_id);
alter table clan_invites add constraint fk_clan_invites_sender_id foreign key (sender_id) references profiles (id) on delete cascade on update restrict;

create index ix_clan_invites_target_id on clan_invites (target_id);
alter table clan_invites add constraint fk_clan_invites_target_id foreign key (target_id) references profiles (id) on delete cascade on update restrict;

create index ix_clan_invites_clan_id on clan_invites (clan_id);
alter table clan_invites add constraint fk_clan_invites_clan_id foreign key (clan_id) references clans (id) on delete cascade on update restrict;

create index ix_clan_profiles_profile_id on clan_profiles (profile_id);
alter table clan_profiles add constraint fk_clan_profiles_profile_id foreign key (profile_id) references profiles (id) on delete cascade on update restrict;

create index ix_clan_profiles_clan_id on clan_profiles (clan_id);
alter table clan_profiles add constraint fk_clan_profiles_clan_id foreign key (clan_id) references clans (id) on delete cascade on update restrict;

create index ix_clan_profile_permissions_clan_profile_id on clan_profile_permissions (clan_profile_id);
alter table clan_profile_permissions add constraint fk_clan_profile_permissions_clan_profile_id foreign key (clan_profile_id) references clan_profiles (id) on delete cascade on update restrict;

alter table profiles add constraint fk_profiles_clan_profile_id foreign key (clan_profile_id) references clan_profiles (id) on delete set null on update restrict;

create index ix_profile_attributes_profile_id on profile_attributes (profile_id);
alter table profile_attributes add constraint fk_profile_attributes_profile_id foreign key (profile_id) references profiles (id) on delete cascade on update restrict;

