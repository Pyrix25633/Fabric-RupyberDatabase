package net.rupyber_studios.rupyber_database_api.util;

import org.jetbrains.annotations.NotNull;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.json.JSONArray;

import static net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI.context;
import static net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI.policeTerminalConfig;
import static net.rupyber_studios.rupyber_database_api.jooq.Tables.*;

public interface Citizen {
    static int selectNumberOfCitizenPages() {
        Field<Integer> count = DSL.count().as("records");
        int records = context.select(count).from(Players).fetchSingle().get(count);
        return (int)Math.ceil((double)records / policeTerminalConfig.recordsPerPage);
    }

    static @NotNull JSONArray selectCitizens(int page, String orderField, boolean orderAscending) {
        Result<Record3<String, String, Boolean>> results = context.select(Players.uuid, Players.username, Players.online)
                .from(Players)
                .orderBy(DSL.field(orderField).sort(orderAscending ? SortOrder.ASC : SortOrder.DESC))
                .limit(page * policeTerminalConfig.recordsPerPage, policeTerminalConfig.recordsPerPage)
                .fetch();
        JSONArray emergencyCalls = new JSONArray();
        for(Record3<String, String, Boolean> record : results)
            emergencyCalls.put(record.intoMap());
        return emergencyCalls;
    }
}