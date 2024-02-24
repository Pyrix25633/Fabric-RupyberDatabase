package net.rupyber_studios.rupyber_database_api.util;

import net.minecraft.util.Pair;
import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface Officer {
    static int selectNumberOfOfficerPages() throws SQLException {
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
        ResultSet result = statement.executeQuery("""
                SELECT COUNT(*) AS records FROM players WHERE rankId IS NOT NULL;""");
        return (int)Math.ceil((double)result.getInt("records") / RupyberDatabaseAPI.policeTerminalConfig.recordsPerPage);
    }

    static @NotNull JSONArray selectOfficers(int page, String orderField, boolean orderAscending) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT uuid, username, online, status, rank, r.color AS rankColor, callsign, callsignReserved
                FROM players AS p
                INNER JOIN ranks AS r
                ON p.rankId=r.id
                ORDER BY #1 #2
                LIMIT ?, ?;"""
                .replace("#1", orderField)
                .replace("#2", orderAscending ? "ASC" : "DESC"));
        preparedStatement.setInt(1, page * RupyberDatabaseAPI.policeTerminalConfig.recordsPerPage);
        preparedStatement.setInt(2, RupyberDatabaseAPI.policeTerminalConfig.recordsPerPage);
        ResultSet result = preparedStatement.executeQuery();
        JSONArray officers = new JSONArray();
        while(result.next()) {
            JSONObject officer = new JSONObject();
            officer.put("uuid", result.getString("uuid"));
            officer.put("username", result.getString("username"));
            officer.put("online", result.getBoolean("online"));
            Status status = Status.fromId(result.getInt("status"));
            assert status != null;
            Pair<String, Integer> statusData = status.getData();
            officer.put("status", statusData.getLeft());
            officer.put("statusColor", statusData.getRight());
            officer.put("rank", result.getString("rank"));
            officer.put("rankColor", result.getInt("rankColor"));
            String callsign = result.getString("callsign");
            officer.put("callsign", callsign != null ? callsign : JSONObject.NULL);
            officer.put("callsignReserved", result.getBoolean("callsignReserved"));
            officers.put(officer);
        }
        return officers;
    }
}