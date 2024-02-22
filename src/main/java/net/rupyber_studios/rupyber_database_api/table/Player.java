package net.rupyber_studios.rupyber_database_api.table;

import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;
import net.rupyber_studios.rupyber_database_api.config.PoliceTerminalConfig;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Player {
    public static void createTable() throws SQLException {
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
        statement.execute("""
                CREATE TABLE IF NOT EXISTS players (
                    id INTEGER PRIMARY KEY,
                    uuid CHAR(36) NOT NULL,
                    username VARCHAR(16) NULL,
                    online BOOLEAN NOT NULL DEFAULT TRUE,
                    status INT NULL DEFAULT NULL,
                    rankId INT NULL DEFAULT NULL,
                    callsign VARCHAR(16) NULL DEFAULT NULL,
                    callsignReserved BOOLEAN NOT NULL DEFAULT FALSE,
                    password CHAR(8) NULL DEFAULT NULL,
                    token CHAR(16) NULL DEFAULT NULL,
                    settings VARCHAR(64) NOT NULL DEFAULT '{"compactMode":false,"condensedFont":false,"sharpMode":false}',
                    UNIQUE (uuid),
                    UNIQUE (username),
                    UNIQUE (callsign),
                    FOREIGN KEY (rankId) REFERENCES ranks(id)
                );""");
        statement.close();
    }

    public static void updateTableWithNewRankIds(PoliceTerminalConfig config, @NotNull List<Integer> missingRankIds)
            throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                UPDATE players
                SET rankId=?
                WHERE rankId=?;""");
        for(int rankId : missingRankIds) {
            Integer nearestRankId = 0;
            for(Rank existingRank : config.ranks) {
                if(existingRank.id < rankId && (existingRank.id - rankId) < (nearestRankId - rankId))
                    nearestRankId = existingRank.id;
            }
            if(nearestRankId == 0) nearestRankId = null;
            preparedStatement.setObject(1, nearestRankId);
            preparedStatement.setInt(2, rankId);
            preparedStatement.execute();
        }
        preparedStatement.close();
    }
}