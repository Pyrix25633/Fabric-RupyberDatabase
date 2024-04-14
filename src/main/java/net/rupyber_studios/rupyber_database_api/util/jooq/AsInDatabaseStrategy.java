package net.rupyber_studios.rupyber_database_api.util.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.meta.Definition;

public class AsInDatabaseStrategy extends DefaultGeneratorStrategy {
    @Override
    public String getJavaClassName(Definition definition, @NotNull Mode mode) {
        return switch(mode) {
            case RECORD -> definition.getOutputName() + "Record";
            case DEFAULT -> definition.getOutputName() + "Table";
            default -> definition.getOutputName();
        };
    }

    @Override
    public String getJavaIdentifier(@NotNull Definition definition) {
        return definition.getOutputName();
    }
}