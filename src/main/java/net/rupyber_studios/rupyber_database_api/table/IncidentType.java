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

public class IncidentType {
    @ConfigEntry.Gui.Excluded
    public static HashMap<Integer, IncidentType> incidentTypes;

    public int id = 10;
    public String code = "New Code";
    @ConfigEntry.ColorPicker
    public int color = 0x55FF55;
    public String description = "Description";

    public IncidentType() {}

    public IncidentType(int id, String code, int color, String description) {
        this.id = id;
        this.code = code;
        this.color = color;
        this.description = description;
    }

    public static void createTable() {
        context.createTableIfNotExists(IncidentTypes).execute();
    }

    public static void updateTableFromConfig(PoliceTerminalConfig config) {
        Result<Record1<Integer>> missingIncidentTypeIds = selectMissingIncidentTypeIdsFromTable(config);
        replaceIntoTable(config);
        Incident.updateTableWithNewIncidentTypeIds(config, missingIncidentTypeIds);
        deleteMissingIncidentTypeIdsFromTable(missingIncidentTypeIds);
    }

    private static @NotNull Result<Record1<Integer>> selectMissingIncidentTypeIdsFromTable(@NotNull PoliceTerminalConfig config) {
        List<Integer> incidentTypeIds = new ArrayList<>();
        for(IncidentType incidentType : config.incidentTypes)
            incidentTypeIds.add(incidentType.id);

        return context.select(IncidentTypes.id)
                .from(IncidentTypes)
                .where(IncidentTypes.id.notIn(incidentTypeIds))
                .fetch();
    }

    private static void replaceIntoTable(@NotNull PoliceTerminalConfig config) {
        for(IncidentType incidentType : config.incidentTypes) {
            context.insertInto(IncidentTypes)
                    .set(IncidentTypes.id, incidentType.id)
                    .set(IncidentTypes.code, incidentType.code)
                    .set(IncidentTypes.color, incidentType.color)
                    .set(IncidentTypes.description, incidentType.description)
                    .onDuplicateKeyUpdate()
                    .set(IncidentTypes.code, incidentType.code)
                    .set(IncidentTypes.color, incidentType.color)
                    .set(IncidentTypes.description, incidentType.description)
                    .execute();
        }
    }

    private static void deleteMissingIncidentTypeIdsFromTable(@NotNull Result<Record1<Integer>> missingIncidentTypeIds) {
        for(Record1<Integer> incidentTypeId : missingIncidentTypeIds) {
            context.deleteFrom(IncidentTypes)
                    .where(IncidentTypes.id.eq(incidentTypeId.value1()))
                    .execute();
        }
    }

    public static void loadIncidentTypes() {
        incidentTypes = new HashMap<>();
        for(IncidentType incidentType : RupyberDatabaseAPI.policeTerminalConfig.incidentTypes) {
            incidentTypes.put(incidentType.id, incidentType);
        }
    }

    public static IncidentType fromId(int id) {
        return incidentTypes.get(id);
    }
}