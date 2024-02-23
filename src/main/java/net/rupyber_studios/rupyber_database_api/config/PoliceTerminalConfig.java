package net.rupyber_studios.rupyber_database_api.config;

import net.rupyber_studios.rupyber_database_api.table.IncidentType;
import net.rupyber_studios.rupyber_database_api.table.Rank;
import net.rupyber_studios.rupyber_database_api.table.ResponseCode;

import java.util.List;

public abstract class PoliceTerminalConfig {
    public List<Rank> ranks;
    public boolean callsignAreaUnitBeat;
    public boolean callsignBeatUnit;
    public boolean callsignUnitBeat;
    public int callsignAreaMin;
    public int callsignAreaMax;
    public int callsignBeatMin;
    public int callsignBeatMax;
    public List<String> callsignUnits;
    public List<ResponseCode> responseCodes;
    public List<IncidentType> incidentTypes;

    public int recordsPerPage;
}