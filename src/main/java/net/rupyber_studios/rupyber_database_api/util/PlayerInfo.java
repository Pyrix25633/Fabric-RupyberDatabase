package net.rupyber_studios.rupyber_database_api.util;

import net.rupyber_studios.rupyber_database_api.table.Rank;

public class PlayerInfo {
    public static PlayerInfo info;

    public Status status;
    public Rank rank;
    public String callsign;

    public PlayerInfo() {
        this.status = null;
        this.rank = null;
        this.callsign = null;
    }

    public PlayerInfo(Status status, Rank rank, String callsign) {
        this.status = status;
        this.rank = rank;
        this.callsign = (callsign == null || callsign.isEmpty()) ? null : callsign;
    }
}