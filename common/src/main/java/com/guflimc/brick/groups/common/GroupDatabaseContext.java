package com.guflimc.brick.groups.common;

import com.guflimc.brick.groups.common.domain.*;
import com.guflimc.brick.orm.ebean.database.EbeanConfig;
import com.guflimc.brick.orm.ebean.database.EbeanDatabaseContext;
import com.guflimc.brick.orm.ebean.database.EbeanMigrations;
import com.guflimc.teams.common.domain.*;
import io.ebean.annotation.Platform;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Arrays;

public class GroupDatabaseContext extends EbeanDatabaseContext {

    private final static String DATASOURCE_NAME = "BrickGroups";

    public GroupDatabaseContext(EbeanConfig config) {
        super(config, DATASOURCE_NAME);
    }

    public GroupDatabaseContext(EbeanConfig config, int poolSize) {
        super(config, DATASOURCE_NAME, poolSize);
    }

    @Override
    protected Class<?>[] applicableClasses() {
        return APPLICABLE_CLASSES;
    }

    private static final Class<?>[] APPLICABLE_CLASSES = new Class[]{
            DGroup.class,
            DMembership.class,
            DMembershipAttribute.class,
            DProfile.class,
            DProfileAttribute.class,
            DGroupInvite.class,
            DGroupAttribute.class,
            DGroupInvite.class
    };

    public static void main(String[] args) throws IOException, SQLException {
        EbeanMigrations generator = new EbeanMigrations(
                DATASOURCE_NAME,
                Path.of("BrickGroups/common/src/main/resources"),
                Platform.H2, Platform.MYSQL
        );
        Arrays.stream(APPLICABLE_CLASSES).forEach(generator::addClass);
        generator.generate();
    }

}
