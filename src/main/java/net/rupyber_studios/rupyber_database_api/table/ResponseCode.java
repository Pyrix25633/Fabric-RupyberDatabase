package net.rupyber_studios.rupyber_database_api.table;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;
import net.rupyber_studios.rupyber_database_api.config.PoliceTerminalConfig;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record1;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI.context;
import static net.rupyber_studios.rupyber_database_api.jooq.Tables.*;

public class ResponseCode {
    @ConfigEntry.Gui.Excluded
    public static HashMap<Integer, ResponseCode> responseCodes;

    public int id = 10;
    public String code = "New Code";
    @ConfigEntry.ColorPicker
    public int color = 0xAAAAAA;
    public String description = "Description";

    public ResponseCode() {}

    public ResponseCode(int id, String code, int color, String description) {
        this.id = id;
        this.code = code;
        this.color = color;
        this.description = description;
    }

    public static void createTable() {
        context.createTableIfNotExists(ResponseCodes).execute();
    }

    public static void updateTableFromConfig(PoliceTerminalConfig config) {
        Result<Record1<Integer>> missingResponseCodeIds = selectMissingResponseCodeIdsFromTable(config);
        replaceIntoTable(config);
        Incident.updateTableWithNewResponseCodeIds(config, missingResponseCodeIds);
        deleteMissingResponseCodeIdsFromTable(missingResponseCodeIds);
    }

    private static @NotNull Result<Record1<Integer>> selectMissingResponseCodeIdsFromTable(@NotNull PoliceTerminalConfig config) {
        List<Integer> responseCodeIds = new ArrayList<>();
        for(ResponseCode responseCode : config.responseCodes)
            responseCodeIds.add(responseCode.id);

        return context.select(ResponseCodes.id)
                .from(ResponseCodes)
                .where(ResponseCodes.id.notIn(responseCodeIds))
                .fetch();
    }

    private static void replaceIntoTable(@NotNull PoliceTerminalConfig config) {
        for(ResponseCode responseCode : config.responseCodes) {
            context.insertInto(ResponseCodes)
                    .set(ResponseCodes.id, responseCode.id)
                    .set(ResponseCodes.code, responseCode.code)
                    .set(ResponseCodes.color, responseCode.color)
                    .set(ResponseCodes.description, responseCode.description)
                    .onDuplicateKeyUpdate()
                    .set(ResponseCodes.code, responseCode.code)
                    .set(ResponseCodes.color, responseCode.color)
                    .set(ResponseCodes.description, responseCode.description)
                    .execute();
        }
    }

    private static void deleteMissingResponseCodeIdsFromTable(@NotNull Result<Record1<Integer>> missingResponseCodeIds) {
        for(Record1<Integer> responseCodeId : missingResponseCodeIds) {
            context.deleteFrom(ResponseCodes)
                    .where(ResponseCodes.id.eq(responseCodeId.value1()))
                    .execute();
        }
    }

    public static void loadResponseCodes() {
        responseCodes = new HashMap<>();
        for(ResponseCode responseCode : RupyberDatabaseAPI.policeTerminalConfig.responseCodes) {
            responseCodes.put(responseCode.id, responseCode);
        }
    }

    public static ResponseCode fromId(int id) {
        return responseCodes.get(id);
    }
}