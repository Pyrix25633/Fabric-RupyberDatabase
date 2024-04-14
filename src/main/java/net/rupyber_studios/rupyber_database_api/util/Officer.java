package net.rupyber_studios.rupyber_database_api.util;

import net.rupyber_studios.rupyber_database_api.jooq.tables.records.PlayersRecord;
import net.rupyber_studios.rupyber_database_api.table.Rank;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI.context;
import static net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI.policeTerminalConfig;
import static net.rupyber_studios.rupyber_database_api.jooq.Tables.*;

public interface Officer {
    // ------
    // Select
    // ------

    @Contract("_ -> new")
    static @Nullable PlayerInfo selectPlayerInfoFromUuid(@NotNull UUID uuid) {
        Record3<Integer, Integer, String> playerInfo = context.select(Players.status, Players.rankId, Players.callsign)
                .from(Players)
                .where(Players.uuid.eq(uuid.toString()))
                .fetchOne();
        if(playerInfo == null) return null;
        return new PlayerInfo(Status.fromId(playerInfo.get(Players.status)),
                Rank.fromId(playerInfo.get(Players.rankId)),
                playerInfo.get(Players.callsign));
    }

    static @Nullable Status selectStatusFromUuid(@NotNull UUID uuid) {
        Record1<Integer> status = context.select(Players.status)
                .from(Players)
                .where(Players.uuid.eq(uuid.toString()))
                .fetchOne();
        if(status == null) return null;
        return Status.fromId(status.value1());
    }

    static @Nullable Rank selectRankFromUuid(@NotNull UUID uuid) {
        Record1<Integer> rank = context.select(Players.rankId)
                .from(Players)
                .where(Players.uuid.eq(uuid.toString()))
                .fetchOne();
        if(rank == null) return null;
        return Rank.fromId(rank.value1());
    }

    static @Nullable String selectCallsignFromUuid(@NotNull UUID uuid) {
        Record1<String> callsign = context.select(Players.callsign)
                .from(Players)
                .where(Players.uuid.eq(uuid.toString()))
                .fetchOne();
        if(callsign == null) return null;
        return callsign.value1();
    }

    static @Nullable Integer selectIdFromCallsign(String callsign) {
        Record1<Integer> id = context.select(Players.id)
                .from(Players)
                .where(Players.callsign.eq(callsign))
                .fetchOne();
        if(id == null) return null;
        return id.value1();
    }

    static @Nullable UUID selectUuidFromCallsign(String callsign) {
        Record1<String> uuid = context.select(Players.uuid)
                .from(Players)
                .where(Players.callsign.eq(callsign))
                .fetchOne();
        if(uuid == null) return null;
        return UUID.fromString(uuid.value1());
    }

    static @NotNull @Unmodifiable List<UUID> selectUuidsFromCallsignLike(String callsign) {
        Result<Record1<String>> result = context.select(Players.uuid)
                .from(Players)
                .where(Players.callsign.like(callsign))
                .fetch();
        List<UUID> uuids = new ArrayList<>();
        for(Record1<String> record : result)
            uuids.add(UUID.fromString(record.value1()));
        return uuids;
    }

    static int selectNumberOfOfficerPages() {
        Field<Integer> count = DSL.count().as("records");
        int records = context.select(count)
                .from(Players)
                .where(Players.rankId.isNotNull())
                .fetchSingle().get(count);
        return (int)Math.ceil((double)records / policeTerminalConfig.recordsPerPage);
    }

    static @NotNull JSONArray selectOfficers(int page, String orderField, boolean orderAscending) {
        Result<Record3<PlayersRecord, String, Integer>> results = context.select(Players, Ranks.rank, Ranks.color.as("rankColor"))
                .from(Players)
                .innerJoin(Ranks)
                .on(Ranks.id.eq(Players.rankId))
                .orderBy(DSL.field(orderField).sort(orderAscending ? SortOrder.ASC : SortOrder.DESC))
                .limit(page * policeTerminalConfig.recordsPerPage, policeTerminalConfig.recordsPerPage)
                .fetch();
        JSONArray emergencyCalls = new JSONArray();
        for(Record3<PlayersRecord, String, Integer> record : results)
            emergencyCalls.put(record.intoMap());
        return emergencyCalls;
    }

    static @Nullable UUID selectAvailableEmergencyOperator() {
        Record1<String> uuid = context.select(Players.uuid)
                .from(Players)
                .where(Players.online.eq(true)
                        .and(Players.status.eq(2))
                        .and(Players.rankId.in(context.select(Ranks.id)
                                .from(Ranks)
                                .where(Ranks.emergencyOperator.eq(true)))))
                .orderBy(DSL.rand())
                .limit(1)
                .fetchOne();
        if(uuid == null) return null;
        return UUID.fromString(uuid.value1());
    }

    // ------
    // Update
    // ------

    static void updateStatusWhereUuid(@NotNull UUID uuid, @Nullable Status status) {
        context.update(Players)
                .set(Players.status, status != null ? status.getId() : null)
                .where(Players.uuid.eq(uuid.toString()))
                .execute();
    }

    static void updateRankWhereUuid(@NotNull UUID uuid, @Nullable Rank rank) {
        context.update(Players)
                .set(Players.rankId, rank != null ? rank.id : null)
                .where(Players.uuid.eq(uuid.toString()))
                .execute();
    }

    static void updateCallsignFromUuid(@NotNull UUID uuid, @Nullable String callsign, boolean reserved) {
        context.update(Players)
                .set(Players.callsign, callsign)
                .where(Players.uuid.eq(uuid.toString()))
                .execute();
    }

    // --------------
    // Authentication
    // --------------

    static @NotNull String initPasswordFromUuid(@NotNull UUID uuid) {
        String password = Credentials.generatePassword();
        context.update(Players)
                .set(Players.password, password)
                .set(Players.token, DSL.value(null, Players.token))
                .where(Players.uuid.eq(uuid.toString()))
                .execute();
        return password;
    }

    static @NotNull String initToken(int id) {
        String token = Credentials.generateToken();
        context.update(Players)
                .set(Players.token, token)
                .set(Players.password, DSL.value(null, Players.password))
                .where(Players.id.eq(id))
                .execute();
        return token;
    }

    static boolean isPasswordCorrect(int id, String password) {
        if(password == null) return false;
        return context.select(Players.id)
                .from(Players)
                .where(Players.id.eq(id).and(Players.password.eq(password)))
                .fetchOne() != null;
    }

    static boolean isTokenCorrect(int id, String token) {
        if(token == null) return false;
        return context.select(Players.id)
                .from(Players)
                .where(Players.id.eq(id).and(Players.token.eq(token)))
                .fetchOne() != null;
    }
}