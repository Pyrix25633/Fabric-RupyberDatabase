package net.rupyber_studios.rupyber_database_api.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;
import net.rupyber_studios.rupyber_database_api.table.IncidentType;
import net.rupyber_studios.rupyber_database_api.table.Rank;
import net.rupyber_studios.rupyber_database_api.table.ResponseCode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class PoliceTerminalConfig {
    // duty

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("The list of ranks used in the duty system, id and rank must be unique [do not use 0 as Java cannot distinguish it from NULL (civilian)!], highers ones are higher in the chain of command")
    public List<Rank> ranks;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("If true callsigns like 7-Adam-22 will be used")
    public boolean callsignAreaUnitBeat;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("If true callsigns like 20-David will be used")
    public boolean callsignBeatUnit;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("If true callsigns like Sam-81 will be used")
    public boolean callsignUnitBeat;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("The minimum for the area number that is $-Adam-22")
    public int callsignAreaMin;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("The maximum for the area number that is $-Adam-22")
    public int callsignAreaMax;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("The minimum for the beat number that is 7-Adam-$, $-David and Sam-$")
    public int callsignBeatMin;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("The maximum for the beat number that is 7-Adam-$, $-David and Sam-$")
    public int callsignBeatMax;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("The unit letters used in callsigns that are 7-$-22, 20-$ and $-81")
    public List<String> callsignUnits;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("The list of response codes used in the duty system, id and code must be unique [do not use 0 as Java cannot distinguish it from NULL!]")
    public List<ResponseCode> responseCodes;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("The list of incident types used in the duty system, id and code must be unique [do not use 0 as Java cannot distinguish it from NULL!]")
    public List<IncidentType> incidentTypes;

    // web

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("web")
    @Comment("The number of records per page displayed in tables")
    @ConfigEntry.BoundedDiscrete(min = 5, max = 50)
    public int recordsPerPage;

    public static void load(String configPath) throws IOException {
        InputStream configInput = new FileInputStream(configPath);
        JSONObject configJson = new JSONObject(new String(configInput.readAllBytes()));
        PoliceTerminalConfig config = new PoliceTerminalConfig() {};
        config.callsignAreaUnitBeat = configJson.getBoolean("callsignAreaUnitBeat");
        config.callsignBeatUnit = configJson.getBoolean("callsignBeatUnit");
        config.callsignUnitBeat = configJson.getBoolean("callsignUnitBeat");
        config.callsignAreaMin = configJson.getInt("callsignAreaMin");
        config.callsignAreaMax = configJson.getInt("callsignAreaMax");
        config.callsignBeatMin = configJson.getInt("callsignBeatMin");
        config.callsignBeatMax = configJson.getInt("callsignBeatMax");
        JSONArray ranks = configJson.getJSONArray("ranks");
        config.ranks = new ArrayList<>();
        for(Object o : ranks) {
            if(o instanceof JSONObject rank)
                config.ranks.add(new Rank(rank.getInt("id"), rank.getString("rank"), rank.getInt("color")));
        }
        JSONArray callsignUnits = configJson.getJSONArray("callsignUnits");
        config.callsignUnits = new ArrayList<>();
        for(Object o : callsignUnits) {
            if(o instanceof String callsignUnit)
                config.callsignUnits.add(callsignUnit);
        }
        JSONArray responseCodes = configJson.getJSONArray("responseCodes");
        config.responseCodes = new ArrayList<>();
        for(Object o : responseCodes) {
            if(o instanceof JSONObject responseCode)
                config.responseCodes.add(new ResponseCode(responseCode.getInt("id"),
                        responseCode.getString("code"), responseCode.getInt("color"),
                        responseCode.getString("description")));
        }
        JSONArray incidentTypes = configJson.getJSONArray("incidentTypes");
        config.incidentTypes = new ArrayList<>();
        for(Object o : incidentTypes) {
            if(o instanceof JSONObject incidentType)
                config.incidentTypes.add(new IncidentType(incidentType.getInt("id"),
                        incidentType.getString("code"), incidentType.getInt("color"),
                        incidentType.getString("description")));
        }
        config.recordsPerPage = configJson.getInt("recordsPerPage");
        RupyberDatabaseAPI.setPoliceTerminalConfig(config);
    }
}