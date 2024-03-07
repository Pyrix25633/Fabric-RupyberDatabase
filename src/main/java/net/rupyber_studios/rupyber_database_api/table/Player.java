package net.rupyber_studios.rupyber_database_api.table;

import net.minecraft.util.Pair;
import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;
import net.rupyber_studios.rupyber_database_api.config.PoliceTerminalConfig;
import net.rupyber_studios.rupyber_database_api.util.PlayerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

public class Player {
    public int id;
    public UUID uuid;
    public String username;
    public boolean online;
    public PlayerInfo info;
    public boolean callsignReserved;

    public Player(UUID uuid, String username, boolean online, PlayerInfo info, boolean callsignReserved) {
        this(0, uuid, username, online, info, callsignReserved);
    }

    public Player(int id, UUID uuid, String username, boolean online, PlayerInfo info, boolean callsignReserved) {
        this.id = id;
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

    public static @Nullable Integer selectIdFromUuid(@NotNull UUID uuid) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT id
                FROM players
                WHERE uuid=?;""");
        preparedStatement.setString(1, uuid.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(result.next()) return result.getInt("id");
        preparedStatement.close();
        return null;
    }

    public static @Nullable UUID selectUuidFromId(int id) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT uuid
                FROM players
                WHERE id=?;""");
        preparedStatement.setInt(1, id);
        ResultSet result = preparedStatement.executeQuery();
        if(result.next()) return UUID.fromString(result.getString("uuid"));
        preparedStatement.close();
        return null;
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
                SELECT id, uuid
                FROM players
                WHERE username=?;""");
        preparedStatement.setString(1, username);
        ResultSet result = preparedStatement.executeQuery();
        if(result.next()) return new Pair<>(result.getInt("id"), UUID.fromString(result.getString("uuid")));
        preparedStatement.close();
        return null;
    }

    private static void updateOnlineTrue(int id) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                        UPDATE players
                        SET online=TRUE
                        WHERE id=?;""");
        preparedStatement.setInt(1, id);
        preparedStatement.execute();
        preparedStatement.close();
    }

    private static void updateUsernameNull(int id) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                        UPDATE players
                        SET username=NULL
                        WHERE id=?;""");
        preparedStatement.setInt(1, id);
        preparedStatement.execute();
        preparedStatement.close();
    }

    private static void insert(@NotNull UUID uuid, @NotNull String username) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                    INSERT INTO players
                    (uuid, username)
                    VALUES (?, ?);""");
        preparedStatement.setString(1, uuid.toString());
        preparedStatement.setString(2, username);
        preparedStatement.execute();
        preparedStatement.close();
    }

    private static @Nullable Pair<Integer, Pair<Integer, Boolean>> selectIdAndRankIdAndCallsignReservedFromUuid(
            @NotNull UUID uuid)
            throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT id, rankId, callsignReserved
                FROM players
                WHERE uuid=?;""");
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
                queryString = """
                        UPDATE players
                        SET status=1, password=NULL, token=NULL
                        WHERE id=?;""";
            else
                queryString = """
                        UPDATE players
                        SET status=1, callsign=NULL, password=NULL, token=NULL
                        WHERE id=?;""";
            PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement(queryString);
            preparedStatement.setInt(1, idRankIdCallsignReserved.getLeft());
            preparedStatement.execute();
            preparedStatement.close();
        }
    }

    public static void handleShutdown() throws SQLException {
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
        statement.execute("""
                UPDATE players
                SET online=FALSE, password=NULL, token=NULL;""");
        statement.execute("""
                UPDATE players
                SET callsign=NULL
                WHERE callsignReserved=FALSE;""");
        statement.close();
    }
}