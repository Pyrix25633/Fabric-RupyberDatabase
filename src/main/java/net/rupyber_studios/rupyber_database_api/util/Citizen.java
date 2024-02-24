package net.rupyber_studios.rupyber_database_api.util;

import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface Citizen {
    static int selectNumberOfCitizenPages() throws SQLException {
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
        ResultSet result = statement.executeQuery("""
                SELECT COUNT(*) AS records FROM players;""");
        return (int)Math.ceil((double)result.getInt("records") / RupyberDatabaseAPI.policeTerminalConfig.recordsPerPage);
    }

    static @NotNull JSONArray selectCitizens(int page, String orderField, boolean orderAscending) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT uuid, username, online
                FROM players
                ORDER BY #1 #2
                LIMIT ?, ?;"""
                .replace("#1", orderField)
                .replace("#2", orderAscending ? "ASC" : "DESC"));
        preparedStatement.setInt(1, page * RupyberDatabaseAPI.policeTerminalConfig.recordsPerPage);
        preparedStatement.setInt(2, RupyberDatabaseAPI.policeTerminalConfig.recordsPerPage);
        ResultSet result = preparedStatement.executeQuery();
        JSONArray citizens = new JSONArray();
        while(result.next()) {
            JSONObject citizen = new JSONObject();
            citizen.put("uuid", result.getString("uuid"));
            citizen.put("username", result.getString("username"));
            citizen.put("online", result.getBoolean("online"));
            citizens.put(citizen);
        }
        return citizens;
    }
}