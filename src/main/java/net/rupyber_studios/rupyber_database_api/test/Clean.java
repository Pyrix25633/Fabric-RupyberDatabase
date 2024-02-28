package net.rupyber_studios.rupyber_database_api.test;

import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;

import java.nio.file.Path;
import java.sql.SQLException;
import java.sql.Statement;

public class Clean {
    public static void main(String[] args) throws SQLException {
        RupyberDatabaseAPI.connectIfNotConnected(Path.of("./test/."));
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
        statement.execute("""
                DROP TABLE players;""");
        statement.close();
        RupyberDatabaseAPI.createPoliceTerminalTables();
        RupyberDatabaseAPI.disconnectIfConnected();
    }
}