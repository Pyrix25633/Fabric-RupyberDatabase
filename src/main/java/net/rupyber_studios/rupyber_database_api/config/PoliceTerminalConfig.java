package net.rupyber_studios.rupyber_database_api.config;

import net.rupyber_studios.rupyber_database_api.table.IncidentType;
import net.rupyber_studios.rupyber_database_api.table.Rank;
import net.rupyber_studios.rupyber_database_api.table.ResponseCode;

import java.util.List;

public abstract class PoliceTerminalConfig {
    public List<Rank> ranks;
    public List<String> callsignUnits;
    public List<ResponseCode> responseCodes;
    public List<IncidentType> incidentTypes;

    public int recordsPerPage;
}