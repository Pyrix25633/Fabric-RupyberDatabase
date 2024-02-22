package net.rupyber_studios.rupyber_database_api.table;

import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;
import net.rupyber_studios.rupyber_database_api.config.PoliceTerminalConfig;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Incident {
    public static void createTable() throws SQLException {
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
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

    public static void updateTableWithNewResponseCodeIds(PoliceTerminalConfig config,
                                                         @NotNull List<Integer> missingResponseCodeIds)
            throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                UPDATE incidents
                SET responseCodeId=?
                WHERE responseCodeId=?;""");
        for(int responseCodeId : missingResponseCodeIds) {
            Integer nearestResponseCodeId = 0;
            for(ResponseCode existingResponseCode : config.responseCodes) {
                if(existingResponseCode.id < responseCodeId &&
                        (existingResponseCode.id - responseCodeId) < (nearestResponseCodeId - responseCodeId))
                    nearestResponseCodeId = existingResponseCode.id;
            }
            if(nearestResponseCodeId == 0) nearestResponseCodeId = null;
            preparedStatement.setObject(1, nearestResponseCodeId);
            preparedStatement.setInt(2, responseCodeId);
            preparedStatement.execute();
        }
        preparedStatement.close();
    }

    public static void updateTableWithNewIncidentTypeIds(PoliceTerminalConfig config,
                                                         @NotNull List<Integer> missingIncidentTypeIds)
            throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                UPDATE incidents
                SET incidentTypeId=?
                WHERE incidentTypeId=?;""");
        for(int incidentTypeId : missingIncidentTypeIds) {
            Integer nearestIncidentTypeId = 0;
            for(IncidentType existingIncidentType : config.incidentTypes) {
                if(existingIncidentType.id < incidentTypeId &&
                        (existingIncidentType.id - incidentTypeId) < (nearestIncidentTypeId - incidentTypeId))
                    nearestIncidentTypeId = existingIncidentType.id;
            }
            if(nearestIncidentTypeId == 0) nearestIncidentTypeId = null;
            preparedStatement.setObject(1, nearestIncidentTypeId);
            preparedStatement.setInt(2, incidentTypeId);
            preparedStatement.execute();
        }
        preparedStatement.close();
    }
}