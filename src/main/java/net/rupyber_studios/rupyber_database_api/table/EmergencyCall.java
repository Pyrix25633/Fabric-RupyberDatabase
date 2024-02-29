package net.rupyber_studios.rupyber_database_api.table;

import net.minecraft.util.math.Vec3d;
import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class EmergencyCall {
    // ------
    // Select
    // ------

    public static int selectNumberOfEmergencyCallPages() throws SQLException {
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
        ResultSet result = statement.executeQuery("""
                SELECT COUNT(*) AS records FROM emergencyCalls;""");
        return (int)Math.ceil((double)result.getInt("records") / RupyberDatabaseAPI.policeTerminalConfig.recordsPerPage);
    }

    public static @NotNull JSONArray selectEmergencyCalls(int page, String orderField, boolean orderAscending) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT callNumber, locationX, locationY, locationZ, createdAt, p.username as caller, closed
                FROM emergencyCalls AS e
                INNER JOIN players AS p
                ON e.callerId=p.id
                ORDER BY #1 #2
                LIMIT ?, ?;"""
                .replace("#1", orderField)
                .replace("#2", orderAscending ? "ASC" : "DESC"));
        preparedStatement.setInt(1, page * RupyberDatabaseAPI.policeTerminalConfig.recordsPerPage);
        preparedStatement.setInt(2, RupyberDatabaseAPI.policeTerminalConfig.recordsPerPage);
        ResultSet result = preparedStatement.executeQuery();
        JSONArray emergencyCalls = new JSONArray();
        while(result.next()) {
            JSONObject emergencyCall = new JSONObject();
            emergencyCall.put("callNumber", result.getInt("callNumber"));
            emergencyCall.put("locationX", result.getInt("locationX"));
            emergencyCall.put("locationY", result.getInt("locationY"));
            emergencyCall.put("locationZ", result.getInt("locationZ"));
            emergencyCall.put("createdAt", result.getString("createdAt"));
            emergencyCall.put("caller", result.getString("caller"));
            emergencyCall.put("closed", result.getBoolean("closed"));
            emergencyCalls.put(emergencyCall);
        }
        return emergencyCalls;
    }

    // ------
    // Insert
    // ------

    public static void insert(@NotNull UUID uuid, @NotNull Vec3d pos, String description) throws SQLException {
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
        ResultSet result = statement.executeQuery("""
                SELECT CURRENT_DATE;""");
        String currentDate = result.getString("CURRENT_DATE");
        int number = EmergencyCallNumber.getNewCallNumber(currentDate);
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                INSERT INTO emergencyCalls
                (callNumber, locationX, locationY, locationZ, callerId, description)
                VALUES (?, ?, ?, ?, (SELECT id FROM players WHERE uuid=?), ?);""");
        preparedStatement.setInt(1, number);
        preparedStatement.setInt(2, (int)pos.x);
        preparedStatement.setInt(3, (int)pos.y);
        preparedStatement.setInt(4, (int)pos.z);
        preparedStatement.setString(5, uuid.toString());
        preparedStatement.setString(6, description);
        preparedStatement.execute();
        preparedStatement.close();
    }

    // -------
    // Startup
    // -------

    public static void createTable() throws SQLException {
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
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