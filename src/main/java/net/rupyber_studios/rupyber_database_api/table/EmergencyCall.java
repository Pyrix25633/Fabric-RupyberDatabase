package net.rupyber_studios.rupyber_database_api.table;

import net.minecraft.util.math.Vec3d;
import net.rupyber_studios.rupyber_database_api.jooq.tables.PlayersTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.json.JSONArray;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI.context;
import static net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI.policeTerminalConfig;
import static net.rupyber_studios.rupyber_database_api.jooq.Tables.*;

public class EmergencyCall {
    public int id;
    public int callNumber;
    public Vec3d location;
    public LocalDateTime createdAt;
    public int callerId;
    public int responderId;
    public LocalDateTime closedAt;
    public String description;

    public EmergencyCall(int id, int callNumber, Vec3d location, LocalDateTime createdAt, int callerId, int responderId,
                         LocalDateTime closedAt, String description) {
        this.id = id;
        this.callNumber = callNumber;
        this.location = location;
        this.createdAt = createdAt;
        this.callerId = callerId;
        this.responderId = responderId;
        this.closedAt = closedAt;
        this.description = description;
    }

    // ------
    // Select
    // ------

    public static @Nullable EmergencyCall selectWhereCallNumber(int callNumber) {
        Record result = context.select()
                .from(EmergencyCalls)
                .where(EmergencyCalls.callNumber.eq(callNumber).and(EmergencyCalls.closedAt.isNull()))
                .fetchOne();
        if(result == null) return null;
        return new EmergencyCall(
                result.get(EmergencyCalls.id),
                callNumber,
                new Vec3d(result.get(EmergencyCalls.locationX), result.get(EmergencyCalls.locationY),
                        result.get(EmergencyCalls.locationZ)),
                result.get(EmergencyCalls.createdAt),
                result.get(EmergencyCalls.callerId),
                result.get(EmergencyCalls.responderId),
                result.get(EmergencyCalls.closedAt),
                result.get(EmergencyCalls.description)
        );
    }

    public static @NotNull List<Integer> selectCallNumberWhereClosedFalse() {
        Result<Record1<Integer>> result = context.select(EmergencyCalls.callNumber)
                .from(EmergencyCalls)
                .where(EmergencyCalls.closedAt.isNull()).fetch();
        List<Integer> numbers = new ArrayList<>();
        for(Record1<Integer> record : result)
            numbers.add(record.get(EmergencyCalls.callNumber));
        return numbers;
    }

    public static int selectNumberOfEmergencyCallPages() {
        Field<Integer> count = DSL.count().as("records");
        int records = context.select(count).from(EmergencyCalls).fetchSingle().get(count);
        return (int)Math.ceil((double)records / policeTerminalConfig.recordsPerPage);
    }

    public static @NotNull JSONArray selectEmergencyCalls(int page, String orderField, boolean orderAscending) {
        PlayersTable c = Players.as("c");
        PlayersTable r = Players.as("r");
        Field<String> caller = c.username.as("caller");
        Field<String> responder = c.username.as("responder");
        Result<Record9<Integer, Integer, Integer, Integer, Integer, String, String, String, String>> results = context.select(
                        EmergencyCalls.id, EmergencyCalls.callNumber, EmergencyCalls.locationX, EmergencyCalls.locationY,
                        EmergencyCalls.locationZ, EmergencyCalls.createdAt.cast(DSL.value(String.class)), caller, responder,
                        EmergencyCalls.closedAt.cast(DSL.value(String.class)))
                .from(EmergencyCalls)
                .innerJoin(c)
                .on(EmergencyCalls.callerId.eq(c.id))
                .innerJoin(r)
                .on(EmergencyCalls.callerId.eq(r.id))
                .orderBy(DSL.field(orderField).sort(orderAscending ? SortOrder.ASC : SortOrder.DESC))
                .limit(page * policeTerminalConfig.recordsPerPage, policeTerminalConfig.recordsPerPage)
                .fetch();
        JSONArray emergencyCalls = new JSONArray();
        for(Record9<Integer, Integer, Integer, Integer, Integer, String, String, String, String> record : results)
            emergencyCalls.put(record.intoMap());
        return emergencyCalls;
    }

    // ------
    // Update
    // ------

    public static void updateClosedTrue(int id) {
        context.update(EmergencyCalls)
                .set(EmergencyCalls.closedAt, context.select(DSL.currentLocalDateTime()))
                .where(EmergencyCalls.id.eq(id))
                .execute();
    }

    // ------
    // Insert
    // ------

    public static int insertAndReturnCallNumber(@NotNull UUID callerUuid, @NotNull UUID responderUuid,
                                                @NotNull Vec3d pos, String description) {
        Record1<Date> currentDate = context.select(DSL.currentDate()).fetchSingle();
        int number = EmergencyCallNumber.getNewCallNumber(currentDate.value1().toLocalDate());
        context.insertInto(EmergencyCalls)
                .set(EmergencyCalls.callNumber, number)
                .set(EmergencyCalls.locationX, (int)pos.x)
                .set(EmergencyCalls.locationY, (int)pos.y)
                .set(EmergencyCalls.locationZ, (int)pos.z)
                .set(EmergencyCalls.callerId,
                        context.select(Players.id).from(Players).where(Players.uuid.eq(callerUuid.toString())))
                .set(EmergencyCalls.responderId,
                        context.select(Players.id).from(Players).where(Players.uuid.eq(responderUuid.toString())))
                .set(EmergencyCalls.description, description)
                .execute();
        return number;
    }

    // -------
    // Startup
    // -------

    public static void createTable() {
        if(!context.meta().getTables().contains(EmergencyCalls))
            context.ddl(EmergencyCalls).executeBatch();
    }
}