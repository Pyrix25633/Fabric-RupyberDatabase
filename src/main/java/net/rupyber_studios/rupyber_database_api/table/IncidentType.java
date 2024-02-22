package net.rupyber_studios.rupyber_database_api.table;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;
import net.rupyber_studios.rupyber_database_api.config.PoliceTerminalConfig;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IncidentType {
    @ConfigEntry.Gui.Excluded
    public static HashMap<Integer, IncidentType> incidentTypes;

    public int id = 10;
    public String code = "New Code";
    @ConfigEntry.ColorPicker
    public int color = 0x55FF55;
    public String description = "Description";

    public IncidentType() {}

    public IncidentType(int id, String code, int color, String description) {
        this.id = id;
        this.code = code;
        this.color = color;
        this.description = description;
    }

    public static void createTable() throws SQLException {
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
        statement.execute("""
                CREATE TABLE IF NOT EXISTS incidentTypes (
                    id INT,
                    code VARCHAR(8) NOT NULL,
                    color INT NOT NULL,
                    description VARCHAR(64) NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE (code)
                );""");
        statement.close();
    }

    public static void updateTableFromConfig(PoliceTerminalConfig config) throws SQLException {
        List<Integer> missingIncidentTypeIds = selectMissingIncidentTypeIdsFromTable(config);
        replaceIntoTable(config);
        Incident.updateTableWithNewIncidentTypeIds(config, missingIncidentTypeIds);
        deleteMissingIncidentTypeIdsFromTable(missingIncidentTypeIds);
    }

    private static @NotNull List<Integer> selectMissingIncidentTypeIdsFromTable(@NotNull PoliceTerminalConfig config)
            throws SQLException {
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
        StringBuilder query = new StringBuilder("SELECT id FROM incidentTypes WHERE id NOT IN (");

        int size = config.incidentTypes.size();
        for(int i = 0; i < size - 1; i++)
            query.append(config.incidentTypes.get(i).id).append(", ");
        query.append(config.incidentTypes.get(size - 1)).append(");");

        ResultSet result = statement.executeQuery(query.toString());
        ArrayList<Integer> missingIncidentTypeIds = new ArrayList<>();
        while(result.next())
            missingIncidentTypeIds.add(result.getInt("id"));
        statement.close();

        return missingIncidentTypeIds;
    }

    private static void replaceIntoTable(@NotNull PoliceTerminalConfig config) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                REPLACE INTO incidentTypes
                (id, code, color, description)
                VALUES (?, ?, ?, ?);""");
        for(IncidentType incidentType : config.incidentTypes) {
            preparedStatement.setInt(1, incidentType.id);
            preparedStatement.setString(2, incidentType.code);
            preparedStatement.setInt(3, incidentType.color);
            preparedStatement.setString(4, incidentType.description);
            preparedStatement.execute();
        }
        preparedStatement.close();
    }

    private static void deleteMissingIncidentTypeIdsFromTable(@NotNull List<Integer> missingIncidentTypeIds)
            throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                DELETE FROM incidentTypes
                WHERE id=?;""");
        for(int incidentTypeId : missingIncidentTypeIds) {
            preparedStatement.setInt(1, incidentTypeId);
            preparedStatement.execute();
        }
        preparedStatement.close();
    }

    public static void loadIncidentTypes(@NotNull PoliceTerminalConfig config) {
        incidentTypes = new HashMap<>();
        for(IncidentType incidentType : config.incidentTypes) {
            incidentTypes.put(incidentType.id, incidentType);
        }
    }

    public static IncidentType fromId(int id) {
        return incidentTypes.get(id);
    }
}