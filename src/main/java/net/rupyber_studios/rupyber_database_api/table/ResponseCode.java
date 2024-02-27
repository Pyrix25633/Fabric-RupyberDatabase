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

public class ResponseCode {
    @ConfigEntry.Gui.Excluded
    public static HashMap<Integer, ResponseCode> responseCodes;

    public int id = 10;
    public String code = "New Code";
    @ConfigEntry.ColorPicker
    public int color = 0xAAAAAA;
    public String description = "Description";

    public ResponseCode() {}

    public ResponseCode(int id, String code, int color, String description) {
        this.id = id;
        this.code = code;
        this.color = color;
        this.description = description;
    }

    public static void createTable() throws SQLException {
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
        statement.execute("""
                CREATE TABLE IF NOT EXISTS responseCodes (
                    id INT,
                    code VARCHAR(16) NOT NULL,
                    color INT NOT NULL,
                    description VARCHAR(64) NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE (code)
                );""");
        statement.close();
    }

    public static void updateTableFromConfig(PoliceTerminalConfig config) throws SQLException {
        List<Integer> missingResponseCodeIds = selectMissingResponseCodeIdsFromTable(config);
        replaceIntoTable(config);
        Incident.updateTableWithNewResponseCodeIds(config, missingResponseCodeIds);
        deleteMissingResponseCodeIdsFromTable(missingResponseCodeIds);
    }

    private static @NotNull List<Integer> selectMissingResponseCodeIdsFromTable(@NotNull PoliceTerminalConfig config)
            throws SQLException {
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
        StringBuilder query = new StringBuilder("SELECT id FROM responseCodes WHERE id NOT IN (");

        int size = config.responseCodes.size();
        for(int i = 0; i < size - 1; i++)
            query.append(config.responseCodes.get(i).id).append(", ");
        query.append(config.responseCodes.get(size - 1).id).append(");");

        ResultSet result = statement.executeQuery(query.toString());
        ArrayList<Integer> missingResponseCodeIds = new ArrayList<>();
        while(result.next())
            missingResponseCodeIds.add(result.getInt("id"));
        statement.close();

        return missingResponseCodeIds;
    }

    private static void replaceIntoTable(@NotNull PoliceTerminalConfig config) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                REPLACE INTO responseCodes
                (id, code, color, description)
                VALUES (?, ?, ?, ?);""");
        for(ResponseCode responseCode : config.responseCodes) {
            preparedStatement.setInt(1, responseCode.id);
            preparedStatement.setString(2, responseCode.code);
            preparedStatement.setInt(3, responseCode.color);
            preparedStatement.setString(4, responseCode.description);
            preparedStatement.execute();
        }
        preparedStatement.close();
    }

    private static void deleteMissingResponseCodeIdsFromTable(@NotNull List<Integer> missingResponseCodeIds)
            throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                DELETE FROM responseCodes
                WHERE id=?;""");
        for(int responseCodeId : missingResponseCodeIds) {
            preparedStatement.setInt(1, responseCodeId);
            preparedStatement.execute();
        }
        preparedStatement.close();
    }

    public static void loadResponseCodes() {
        responseCodes = new HashMap<>();
        for(ResponseCode responseCode : RupyberDatabaseAPI.policeTerminalConfig.responseCodes) {
            responseCodes.put(responseCode.id, responseCode);
        }
    }

    public static ResponseCode fromId(int id) {
        return responseCodes.get(id);
    }
}