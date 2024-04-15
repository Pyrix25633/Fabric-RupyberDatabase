package net.rupyber_studios.rupyber_database_api.table;

import static net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI.context;
import static net.rupyber_studios.rupyber_database_api.jooq.Tables.*;

public interface IncidentPlayer {
    static void createTable() {
        if(!context.meta().getTables().contains(IncidentPlayers))
            context.ddl(IncidentPlayers).executeBatch();
    }
}