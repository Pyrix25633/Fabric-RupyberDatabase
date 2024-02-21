package net.rupyber_studios.rupyber_database.config;

import net.rupyber_studios.rupyber_database.table.IncidentType;
import net.rupyber_studios.rupyber_database.table.Rank;
import net.rupyber_studios.rupyber_database.table.ResponseCode;

import java.util.List;

public abstract class PoliceTerminalConfig {
    public List<Rank> ranks;
    public List<String> callsignUnits;
    public List<ResponseCode> responseCodes;
    public List<IncidentType> incidentTypes;

    public int recordsPerPage;
}