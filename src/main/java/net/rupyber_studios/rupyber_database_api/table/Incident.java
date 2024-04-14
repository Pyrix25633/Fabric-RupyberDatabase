package net.rupyber_studios.rupyber_database_api.table;

import net.rupyber_studios.rupyber_database_api.config.PoliceTerminalConfig;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record1;
import org.jooq.Result;

import static net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI.context;
import static net.rupyber_studios.rupyber_database_api.jooq.Tables.*;

public class Incident {
    public static void createTable() {
        context.createTableIfNotExists(Incidents).execute();
    }

    public static void updateTableWithNewResponseCodeIds(PoliceTerminalConfig config,
                                                         @NotNull Result<Record1<Integer>> missingResponseCodeIds) {
        for(Record1<Integer> responseCodeIdRecord : missingResponseCodeIds) {
            int responseCodeId = responseCodeIdRecord.value1();
            Integer nearestResponseCodeId = 0;
            for(ResponseCode existingResponseCode : config.responseCodes) {
                if(existingResponseCode.id < responseCodeId &&
                        (existingResponseCode.id - responseCodeId) < (nearestResponseCodeId - responseCodeId))
                    nearestResponseCodeId = existingResponseCode.id;
            }
            if(nearestResponseCodeId == 0) nearestResponseCodeId = null;
            context.update(Incidents)
                    .set(Incidents.responseCodeId, nearestResponseCodeId)
                    .where(Incidents.responseCodeId.eq(responseCodeId))
                    .execute();
        }
    }

    public static void updateTableWithNewIncidentTypeIds(PoliceTerminalConfig config,
                                                         @NotNull Result<Record1<Integer>> missingIncidentTypeIds) {
        for(Record1<Integer> incidentTypeIdRecord : missingIncidentTypeIds) {
            int incidentTypeId = incidentTypeIdRecord.value1();
            Integer nearestIncidentTypeId = 0;
            for(IncidentType existingIncidentType : config.incidentTypes) {
                if(existingIncidentType.id < incidentTypeId &&
                        (existingIncidentType.id - incidentTypeId) < (nearestIncidentTypeId - incidentTypeId))
                    nearestIncidentTypeId = existingIncidentType.id;
            }
            if(nearestIncidentTypeId == 0) nearestIncidentTypeId = null;
            context.update(Incidents)
                    .set(Incidents.incidentTypeId, nearestIncidentTypeId)
                    .where(Incidents.incidentTypeId.eq(incidentTypeId))
                    .execute();
        }
    }
}