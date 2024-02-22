package net.rupyber_studios.rupyber_database_api.table;

import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;

import java.sql.SQLException;
import java.sql.Statement;

public interface IncidentPlayer {
    static void createTable() throws SQLException {
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
        statement.execute("""
                CREATE TABLE IF NOT EXISTS incidentPlayers (
                    incidentId INT NOT NULL,
                    role INT NOT NULL,
                    playerId INT NOT NULL,
                    addedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    removedAt DATETIME NULL DEFAULT NULL,
                    FOREIGN KEY (incidentId) REFERENCES incidents(id),
                    FOREIGN KEY (playerId) REFERENCES players(id)
                );""");
        statement.close();
    }
}