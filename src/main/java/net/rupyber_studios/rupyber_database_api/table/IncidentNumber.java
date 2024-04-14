package net.rupyber_studios.rupyber_database_api.table;

import static net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI.context;
import static net.rupyber_studios.rupyber_database_api.jooq.Tables.*;

public interface IncidentNumber {
    // -------------
    // Handle number
    // -------------

    // -------
    // Startup
    // -------
    static void createTable() {
        context.createTableIfNotExists(IncidentNumbers).execute();
    }
}