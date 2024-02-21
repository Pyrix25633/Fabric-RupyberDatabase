package net.rupyber_studios.rupyber_database.table;

import net.rupyber_studios.rupyber_database.RupyberDatabase;

import java.sql.SQLException;
import java.sql.Statement;

public interface IncidentNumber {
    static void createTable() throws SQLException {
        Statement statement = RupyberDatabase.connection.createStatement();
        statement.execute("""
                CREATE TABLE IF NOT EXISTS incidentNumbers (
                    day DATE,
                    number INT NOT NULL,
                    PRIMARY KEY (day)
                );""");
        statement.close();
    }
}