package net.rupyber_studios.rupyber_database_api.test;

import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;

import java.nio.file.Path;

public class Init {
    public static void main(String[] args) {
        RupyberDatabaseAPI.connectIfNotConnected(Path.of("./test/."));
        RupyberDatabaseAPI.createPoliceTerminalTables();
        RupyberDatabaseAPI.disconnectIfConnected();
    }
}