package net.rupyber_studios.rupyber_database_api.table;

import net.minecraft.util.Pair;
import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;
import net.rupyber_studios.rupyber_database_api.config.PoliceTerminalConfig;
import net.rupyber_studios.rupyber_database_api.util.Credentials;
import net.rupyber_studios.rupyber_database_api.util.PlayerInfo;
import net.rupyber_studios.rupyber_database_api.util.Status;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Player {
    public UUID uuid;
    public String username;
    public boolean online;
    public PlayerInfo info;
    public boolean callsignReserved;

    public Player(UUID uuid, String username, boolean online, PlayerInfo info, boolean callsignReserved) {
        this.uuid = uuid;
        this.username = username;
        this.online = online;
        this.info = info;
        this.callsignReserved = callsignReserved;
    }

    // ------
    // Select
    // ------

    public static String selectUsernameFromUuid(@NotNull UUID uuid) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT username
                FROM players
                WHERE uuid=?;""");
        preparedStatement.setString(1, uuid.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return "";
        String username = result.getString("username");
        return username != null ? username : uuid.toString();
    }

    @Contract("_ -> new")
    public static @NotNull PlayerInfo selectPlayerInfoFromUuid(@NotNull UUID uuid) throws SQLException {
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

    public static @Nullable Status selectStatusFromUuid(@NotNull UUID uuid) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT status
                FROM players
                WHERE uuid=?;""");
        preparedStatement.setString(1, uuid.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return null;
        return Status.fromId(result.getInt("status"));
    }

    public static @Nullable Rank selectRankFromUuid(@NotNull UUID uuid) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT rankId
                FROM players
                WHERE uuid=?;""");
        preparedStatement.setString(1, uuid.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return null;
        return Rank.fromId(result.getInt("rankId"));
    }

    public static @Nullable String selectCallsignFromUuid(@NotNull UUID uuid) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT callsign
                FROM players
                WHERE uuid=?;""");
        preparedStatement.setString(1, uuid.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return null;
        return result.getString("callsign");
    }

    public static @Nullable String selectSettings(int id) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT settings
                FROM players
                WHERE id=?;""");
        preparedStatement.setInt(1, id);
        ResultSet result = preparedStatement.executeQuery();
        if(result.next()) return result.getString("settings");
        preparedStatement.close();
        return null;
    }

    public static int selectIdFromCallsign(String callsign) throws SQLException {
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

    public static @Nullable UUID selectUuidFromCallsign(String callsign) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT uuid
                FROM players
                WHERE callsign=?;""");
        preparedStatement.setString(1, callsign);
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return null;
        return UUID.fromString(result.getString("uuid"));
    }

    public static @Nullable @Unmodifiable List<UUID> selectUuidsFromCallsignLike(String callsign) throws SQLException {
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
        return playerUuids;
    }

    // ------
    // Update
    // ------

    public static void updateStatusFromUuid(@NotNull UUID uuid, @Nullable Status status) throws SQLException {
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

    public static void updateRankFromUuid(@NotNull UUID uuid, @Nullable Rank rank) throws SQLException {
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

    public static void updateCallsignFromUuid(@NotNull UUID uuid, @Nullable String callsign, boolean reserved)
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

    public static @NotNull String initPasswordFromUuid(@NotNull UUID uuid) throws SQLException {
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

    public static @NotNull String initToken(int id) throws SQLException {
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

    public static boolean isPasswordCorrect(int id, String password) throws SQLException {
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

    public static boolean isTokenCorrect(int id, String token) throws SQLException {
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

    // -----------
    // Player join
    // -----------

    public static void insertOrUpdate(UUID uuid, String username) throws SQLException {
        Pair<Integer, UUID> idUuid = selectIdAndUuidFromUsername(username);
        if(idUuid != null) {
            if(idUuid.getRight().equals(uuid)) {
                updateOnlineTrue(idUuid.getLeft());
                return;
            }
            else {
                updateUsernameNull(idUuid.getLeft());
            }
        }
        Integer id = selectIdFromUuid(uuid);
        if(id != null) {
            updateOnlineTrue(id);
        }
        else {
            insert(uuid, username);
        }
    }

    private static @Nullable Pair<Integer, UUID> selectIdAndUuidFromUsername(@NotNull String username) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT id, uuid FROM players WHERE username=?;""");
        preparedStatement.setString(1, username);
        ResultSet result = preparedStatement.executeQuery();
        if(result.next()) return new Pair<>(result.getInt("id"), UUID.fromString(result.getString("uuid")));
        preparedStatement.close();
        return null;
    }

    private static void updateOnlineTrue(int id) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                        UPDATE players SET online=TRUE WHERE id=?;""");
        preparedStatement.setInt(1, id);
        preparedStatement.execute();
        preparedStatement.close();
    }

    private static void updateUsernameNull(int id) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                        UPDATE players SET username=NULL WHERE id=?;""");
        preparedStatement.setInt(1, id);
        preparedStatement.execute();
        preparedStatement.close();
    }

    private static @Nullable Integer selectIdFromUuid(@NotNull UUID uuid) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT id FROM players WHERE uuid=?;""");
        preparedStatement.setString(1, uuid.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(result.next()) return result.getInt("id");
        preparedStatement.close();
        return null;
    }

    private static void insert(@NotNull UUID uuid, @NotNull String username) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                    INSERT INTO players (uuid, username) VALUES (?, ?);""");
        preparedStatement.setString(1, uuid.toString());
        preparedStatement.setString(2, username);
        preparedStatement.execute();
        preparedStatement.close();
    }

    private static @Nullable Pair<Integer, Pair<Integer, Boolean>> selectIdAndRankIdAndCallsignReservedFromUuid(
            @NotNull UUID uuid)
            throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT id, rankId, callsignReserved FROM players WHERE uuid=?;""");
        preparedStatement.setString(1, uuid.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(result.next()) return new Pair<>(result.getInt("id"),
                new Pair<>(result.getInt("rankId"), result.getBoolean("callsignReserved")));
        preparedStatement.close();
        return null;
    }

    // --------------------
    // Startup and shutdown
    // --------------------

    public static void createTable() throws SQLException {
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
        statement.execute("""
                CREATE TABLE IF NOT EXISTS players (
                    id INTEGER PRIMARY KEY,
                    uuid CHAR(36) NOT NULL,
                    username VARCHAR(16) NULL,
                    online BOOLEAN NOT NULL DEFAULT TRUE,
                    status INT NULL DEFAULT NULL,
                    rankId INT NULL DEFAULT NULL,
                    callsign VARCHAR(16) NULL DEFAULT NULL,
                    callsignReserved BOOLEAN NOT NULL DEFAULT FALSE,
                    password CHAR(8) NULL DEFAULT NULL,
                    token CHAR(16) NULL DEFAULT NULL,
                    settings VARCHAR(64) NOT NULL DEFAULT '{"compactMode":false,"condensedFont":false,"sharpMode":false}',
                    UNIQUE (uuid),
                    UNIQUE (username),
                    UNIQUE (callsign),
                    FOREIGN KEY (rankId) REFERENCES ranks(id)
                );""");
        statement.close();
    }

    public static void updateTableWithNewRankIds(PoliceTerminalConfig config, @NotNull List<Integer> missingRankIds)
            throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                UPDATE players
                SET rankId=?
                WHERE rankId=?;""");
        for(int rankId : missingRankIds) {
            Integer nearestRankId = 0;
            for(Rank existingRank : config.ranks) {
                if(existingRank.id < rankId && (existingRank.id - rankId) < (nearestRankId - rankId))
                    nearestRankId = existingRank.id;
            }
            if(nearestRankId == 0) nearestRankId = null;
            preparedStatement.setObject(1, nearestRankId);
            preparedStatement.setInt(2, rankId);
            preparedStatement.execute();
        }
        preparedStatement.close();
    }

    public static void handleDisconnection(@NotNull UUID uuid) throws SQLException {
        Pair<Integer, Pair<Integer, Boolean>> idRankIdCallsignReserved = selectIdAndRankIdAndCallsignReservedFromUuid(uuid);
        if(idRankIdCallsignReserved == null) return;
        if(idRankIdCallsignReserved.getRight().getLeft() != 0) {
            String queryString;
            if(idRankIdCallsignReserved.getRight().getRight())
                queryString = "UPDATE players SET status=1, password=NULL, token=NULL WHERE id=?;";
            else
                queryString = "UPDATE players SET status=1, callsign=NULL, password=NULL, token=NULL WHERE id=?;";
            PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement(queryString);
            preparedStatement.setInt(1, idRankIdCallsignReserved.getLeft());
            preparedStatement.execute();
            preparedStatement.close();
        }
    }

    public static void handleShutdown() throws SQLException {
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
        statement.execute("UPDATE players SET online=FALSE, password=NULL, token=NULL;");
        statement.execute("UPDATE players SET callsign=NULL WHERE callsignReserved=FALSE;");
        statement.close();
    }
}