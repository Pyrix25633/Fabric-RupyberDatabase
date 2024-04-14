package net.rupyber_studios.rupyber_database_api.util.jooq;

import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.*;
import org.jooq.meta.jaxb.Configuration;

import java.io.*;
import java.util.Arrays;

public class JooqGenerate {
    public static void main(String[] args) throws Exception {
        try(FileInputStream reader = new FileInputStream(
                "./src/main/java/net/rupyber_studios/rupyber_database_api/util/jooq/AsInDatabaseStrategy.java")) {
            String code = Arrays.toString(reader.readAllBytes());
            Configuration configuration = new Configuration()
                    .withJdbc(new Jdbc()
                            .withDriver("org.sqlite.JDBC")
                            .withUrl("jdbc:sqlite:/media/pyrix25633/Drive/mods/RupyberDatabaseAPI/workspace/Fabric-RupyberDatabaseAPI/test/.rupyber.db"))
                    .withGenerator(new Generator()
                            .withDatabase(new Database()
                                    .withName("org.jooq.meta.sqlite.SQLiteDatabase")
                                    .withIncludes(".*")
                                    .withExcludes(""))
                            .withTarget(new Target()
                                    .withPackageName("net.rupyber_studios.rupyber_database_api.jooq")
                                    .withDirectory("./src/main/java"))
                            .withStrategy(new Strategy()
                                    .withName("net.rupyber_studios.rupyber_database_api.util.jooq.AsInDatabaseStrategy")
                                    .withJava(code)));
            GenerationTool.generate(configuration);
        }
    }
}