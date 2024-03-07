package net.rupyber_studios.rupyber_database_api.util;

import net.minecraft.util.Pair;
import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;
import net.rupyber_studios.rupyber_database_api.table.Rank;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface Officer {
    // ------
    // Select
    // ------

    @Contract("_ -> new")
    static @NotNull PlayerInfo selectPlayerInfoFromUuid(@NotNull UUID uuid) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT status, rankId, callsign
                FROM players
                WHERE uuid=?;""");
        preparedStatement.setString(1, uuid.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return new PlayerInfo();
        return new PlayerInfo(Status.fromId(result.getInt("status")), Rank.fromId(result.getInt("rankId")),
                result.getString("callsign"));
    }

    static @Nullable Status selectStatusFromUuid(@NotNull UUID uuid) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT status
                FROM players
                WHERE uuid=?;""");
        preparedStatement.setString(1, uuid.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return null;
        return Status.fromId(result.getInt("status"));
    }

    static @Nullable Rank selectRankFromUuid(@NotNull UUID uuid) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT rankId
                FROM players
                WHERE uuid=?;""");
        preparedStatement.setString(1, uuid.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return null;
        return Rank.fromId(result.getInt("rankId"));
    }

    static @Nullable String selectCallsignFromUuid(@NotNull UUID uuid) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT callsign
                FROM players
                WHERE uuid=?;""");
        preparedStatement.setString(1, uuid.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return null;
        return result.getString("callsign");
    }

    static int selectIdFromCallsign(String callsign) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT id
                FROM players
                WHERE callsign=?;""");
        preparedStatement.setString(1, callsign);
        ResultSet result = preparedStatement.executeQuery();
        if(result.next()) return result.getInt("id");
        preparedStatement.close();
        return 0;
    }

    static @Nullable UUID selectUuidFromCallsign(String callsign) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT uuid
                FROM players
                WHERE callsign=?;""");
        preparedStatement.setString(1, callsign);
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return null;
        return UUID.fromString(result.getString("uuid"));
    }

    static @Nullable @Unmodifiable List<UUID> selectUuidsFromCallsignLike(String callsign) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT uuid
                FROM players
                WHERE callsign LIKE ?;""");
        preparedStatement.setString(1, callsign);
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return null;
        List<UUID> playerUuids = new ArrayList<>();
        while(result.next()) {
            playerUuids.add(UUID.fromString(result.getString("uuid")));
        }
        preparedStatement.close();
        return playerUuids;
    }

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
        preparedStatement.close();
        return officers;
    }

    static @Nullable UUID selectAvailableEmergencyOperator() throws SQLException {
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
        ResultSet result = statement.executeQuery("""
                SELECT uuid
                FROM players
                WHERE
                    online=TRUE
                    AND
                    status=2
                    AND rankId IN (
                        SELECT id
                        FROM ranks
                        WHERE emergencyOperator=TRUE
                    )
                ORDER BY RANDOM()
                LIMIT 1;""");
        if(!result.next()) return null;
        return UUID.fromString(result.getString("uuid"));
    }

    // ------
    // Update
    // ------

    static void updateStatusFromUuid(@NotNull UUID uuid, @Nullable Status status) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                UPDATE players
                SET status=?
                WHERE uuid=?;""");
        Integer s = status != null ? status.getId() : null;
        preparedStatement.setObject(1, s);
        preparedStatement.setString(2, uuid.toString());
        preparedStatement.execute();
        preparedStatement.close();
    }

    static void updateRankFromUuid(@NotNull UUID uuid, @Nullable Rank rank) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                UPDATE players
                SET rankId=?
                WHERE uuid=?;""");
        Integer r = rank != null ? rank.id : null;
        preparedStatement.setObject(1, r);
        preparedStatement.setString(2, uuid.toString());
        preparedStatement.execute();
        preparedStatement.close();
    }

    static void updateCallsignFromUuid(@NotNull UUID uuid, @Nullable String callsign, boolean reserved)
            throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                UPDATE players
                SET callsign=?, callsignReserved=?
                WHERE uuid=?;""");
        preparedStatement.setString(1, callsign);
        preparedStatement.setBoolean(2, reserved);
        preparedStatement.setString(3, uuid.toString());
        preparedStatement.execute();
        preparedStatement.close();
    }

    // --------------
    // Authentication
    // --------------

    static @NotNull String initPasswordFromUuid(@NotNull UUID uuid) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                UPDATE players
                SET password=?, token=NULL
                WHERE uuid=?;""");
        String password = Credentials.generatePassword();
        preparedStatement.setString(1, password);
        preparedStatement.setString(2, uuid.toString());
        preparedStatement.execute();
        preparedStatement.close();
        return password;
    }

    static @NotNull String initToken(int id) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                UPDATE players
                SET password=NULL, token=?
                WHERE id=?;""");
        String token = Credentials.generateToken();
        preparedStatement.setString(1, token);
        preparedStatement.setInt(2, id);
        preparedStatement.execute();
        preparedStatement.close();
        return token;
    }

    static boolean isPasswordCorrect(int id, String password) throws SQLException {
        if(password == null) return false;
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT id
                FROM players
                WHERE id=? AND password=?;""");
        preparedStatement.setInt(1, id);
        preparedStatement.setString(2, password);
        ResultSet result = preparedStatement.executeQuery();
        return result.next();
    }

    static boolean isTokenCorrect(int id, String token) throws SQLException {
        if(token == null) return false;
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT id
                FROM players
                WHERE id=? AND token=?;""");
        preparedStatement.setInt(1, id);
        preparedStatement.setString(2, token);
        ResultSet result = preparedStatement.executeQuery();
        return result.next();
    }
}