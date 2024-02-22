package net.rupyber_studios.rupyber_database_api.table;

import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;

import java.sql.SQLException;
import java.sql.Statement;

public interface IncidentNumber {
    static void createTable() throws SQLException {
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
        statement.execute("""
                CREATE TABLE IF NOT EXISTS incidentNumbers (
                    day DATE,
                    number INT NOT NULL,
                    PRIMARY KEY (day)
                );""");
        statement.close();
    }
}