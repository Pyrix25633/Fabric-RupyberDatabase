package net.rupyber_studios.rupyber_database.table;

import net.rupyber_studios.rupyber_database.RupyberDatabase;

import java.sql.SQLException;
import java.sql.Statement;

public class Incident {
    public static void createTable() throws SQLException {
        Statement statement = RupyberDatabase.connection.createStatement();
        statement.execute("""
                CREATE TABLE IF NOT EXISTS incidents (
                    id INTEGER PRIMARY KEY,
                    incidentNumber INT NOT NULL,
                    emergencyCallId INT NULL DEFAULT NULL,
                    priority INT NOT NULL,
                    responseCodeId INT NOT NULL,
                    recipients INT NOT NULL,
                    incidentTypeId INT NOT NULL,
                    locationX INT NOT NULL,
                    locationY INT NOT NULL,
                    locationZ INT NOT NULL,
                    description VARCHAR(128) NULL,
                    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    createdBy INT NOT NULL,
                    closedAt DATETIME NULL DEFAULT NULL,
                    closedBy INT NULL DEFAULT NULL,
                    FOREIGN KEY (emergencyCallId) REFERENCES emergencyCalls(id),
                    FOREIGN KEY (responseCodeId) REFERENCES responseCodes(id),
                    FOREIGN KEY (incidentTypeId) REFERENCES incidentTypes(id),
                    FOREIGN KEY (createdBy) REFERENCES players(id),
                    FOREIGN KEY (closedBy) REFERENCES players(id)
                );""");
        statement.close();
    }
}