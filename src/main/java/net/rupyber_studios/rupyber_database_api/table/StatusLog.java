package net.rupyber_studios.rupyber_database_api.table;

import net.rupyber_studios.rupyber_database_api.util.Status;
import org.jetbrains.annotations.Nullable;

import static net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI.context;
import static net.rupyber_studios.rupyber_database_api.jooq.Tables.StatusLogs;

public class StatusLog {
    // ------
    // Insert
    // ------

    public static void insert(int playerId, @Nullable Status status) {
        context.insertInto(StatusLogs)
                .set(StatusLogs.playerId, playerId)
                .set(StatusLogs.statusId, status != null ? status.getId() : null)
                .execute();
    }

    // -------
    // Startup
    // -------

    public static void createTable() {
        if(!context.meta().getTables().contains(StatusLogs))
            context.ddl(StatusLogs).executeBatch();
    }
}