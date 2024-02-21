package net.rupyber_studios.rupyber_database.table;

import net.rupyber_studios.rupyber_database.RupyberDatabase;

import java.sql.SQLException;
import java.sql.Statement;

public class EmergencyCall {
    public static void createTable() throws SQLException {
        Statement statement = RupyberDatabase.connection.createStatement();
        statement.execute("""
                CREATE TABLE IF NOT EXISTS emergencyCalls (
                    id INTEGER PRIMARY KEY,
                    callNumber INT NOT NULL,
                    locationX INT NOT NULL,
                    locationY INT NOT NULL,
                    locationZ INT NOT NULL,
                    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    callerId INT NOT NULL,
                    closed BOOLEAN NOT NULL DEFAULT FALSE,
                    description VARCHAR(256),
                    FOREIGN KEY (callerId) REFERENCES players(id)
                );""");
        statement.close();
    }
}