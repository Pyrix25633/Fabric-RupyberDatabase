package net.rupyber_studios.rupyber_database_api.table;

import net.rupyber_studios.rupyber_database_api.config.PoliceTerminalConfig;
import net.rupyber_studios.rupyber_database_api.jooq.tables.records.PlayersRecord;
import net.rupyber_studios.rupyber_database_api.util.PlayerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.UUID;

import static net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI.context;
import static net.rupyber_studios.rupyber_database_api.jooq.Tables.*;

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

    public static @Nullable String selectUsernameWhereUuid(@NotNull UUID uuid) {
        Record1<String> username = context.select(Players.username)
                .from(Players)
                .where(Players.uuid.eq(uuid.toString()))
                .fetchOne();
        if(username == null) return null;
        return username.value1();
    }

    public static @Nullable Integer selectIdWhereUuid(@NotNull UUID uuid) {
        Record1<Integer> id = context.select(Players.id)
                .from(Players)
                .where(Players.uuid.eq(uuid.toString()))
                .fetchOne();
        if(id == null) return null;
        return id.value1();
    }

    public static @Nullable UUID selectUuid(int id) {
        Record1<String> uuid = context.select(Players.uuid)
                .from(Players)
                .where(Players.id.eq(id))
                .fetchOne();
        if(uuid == null) return null;
        return UUID.fromString(uuid.value1());
    }

    public static @Nullable String selectSettings(int id) {
        Record1<String> settings = context.select(Players.settings)
                .from(Players)
                .where(Players.id.eq(id))
                .fetchOne();
        if(settings == null) return null;
        return settings.value1();
    }

    // -----------
    // Player join
    // -----------

    public static void insertOrUpdate(UUID uuid, String username) {
        Record2<Integer, String> idUuid = selectIdAndUuidFromUsername(username);
        if(idUuid != null) {
            if(idUuid.get(Players.uuid).equals(uuid.toString())) {
                updateOnlineTrue(idUuid.get(Players.id));
                return;
            }
            else {
                updateUsernameNull(idUuid.get(Players.id));
            }
        }
        Integer id = selectIdWhereUuid(uuid);
        if(id != null) {
            updateOnlineTrue(id);
        }
        else {
            insert(uuid, username);
        }
    }

    private static @Nullable Record2<Integer, String> selectIdAndUuidFromUsername(@NotNull String username) {
        return context.select(Players.id, Players.uuid)
                .from(Players)
                .where(Players.username.eq(username))
                .fetchOne();
    }

    private static void updateOnlineTrue(int id) {
        context.update(Players)
                .set(Players.online, true)
                .where(Players.id.eq(id))
                .execute();
    }

    private static void updateUsernameNull(int id) {
        context.update(Players)
                .set(Players.username, DSL.value(null, Players.username))
                .where(Players.id.eq(id))
                .execute();
    }

    private static void insert(@NotNull UUID uuid, @NotNull String username) {
        context.insertInto(Players)
                .set(Players.uuid, uuid.toString())
                .set(Players.username, username)
                .execute();
    }

    private static @Nullable Record3<Integer, Integer, Boolean> selectIdAndRankIdAndCallsignReservedFromUuid(@NotNull UUID uuid) {
        return context.select(Players.id, Players.rankId, Players.callsignReserved)
                .from(Players)
                .where(Players.uuid.eq(uuid.toString()))
                .fetchOne();
    }

    // --------------------
    // Startup and shutdown
    // --------------------

    public static void createTable() {
        context.createTableIfNotExists(Players).execute();
    }

    public static void updateTableWithNewRankIds(PoliceTerminalConfig config, @NotNull Result<Record1<Integer>> missingRankIds) {
        for(Record1<Integer> rankIdRecord : missingRankIds) {
            int rankId = rankIdRecord.value1();
            Integer nearestRankId = 0;
            for(Rank existingRank : config.ranks) {
                if(existingRank.id < rankId && (existingRank.id - rankId) < (nearestRankId - rankId))
                    nearestRankId = existingRank.id;
            }
            if(nearestRankId == 0) nearestRankId = null;
            context.update(Players)
                    .set(Players.rankId, nearestRankId)
                    .where(Players.rankId.eq(rankId))
                    .execute();
        }
    }

    public static void handleDisconnection(@NotNull UUID uuid) {
        Record3<Integer, Integer, Boolean> idRankIdCallsignReserved = selectIdAndRankIdAndCallsignReservedFromUuid(uuid);
        if(idRankIdCallsignReserved == null) return;
        if(idRankIdCallsignReserved.get(Players.rankId) != 0) {
            UpdateSetMoreStep<PlayersRecord> query = context.update(Players)
                    .set(Players.status, 1)
                    .set(Players.password, DSL.value(null, Players.password))
                    .set(Players.token, DSL.value(null, Players.token));
            if(!idRankIdCallsignReserved.get(Players.callsignReserved))
                query = query.set(Players.callsign, DSL.value(null, Players.callsign));
            query.where(Players.id.eq(idRankIdCallsignReserved.get(Players.id)))
                    .execute();

        }
    }

    public static void handleShutdown() {
        context.update(Players)
                .set(Players.online, false)
                .set(Players.password, DSL.value(null, Players.password))
                .set(Players.token, DSL.value(null, Players.token))
                .execute();
        context.update(Players)
                .set(Players.callsign, DSL.value(null, Players.token))
                .where(Players.callsignReserved.eq(false))
                .execute();
    }
}