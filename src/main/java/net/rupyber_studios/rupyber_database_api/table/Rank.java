package net.rupyber_studios.rupyber_database_api.table;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;
import net.rupyber_studios.rupyber_database_api.config.PoliceTerminalConfig;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Rank {
    @ConfigEntry.Gui.Excluded
    public static HashMap<Integer, Rank> ranks;

    public int id = 10;
    public String rank = "New Rank";
    public boolean emergencyOperator = false;
    @ConfigEntry.ColorPicker
    public int color = 0x5555FF;

    public Rank() {}

    public Rank(int id, String rank, boolean emergencyOperator, int color) {
        this.id = id;
        this.rank = rank;
        this.emergencyOperator = emergencyOperator;
        this.color = color;
    }

    public static void createTable() throws SQLException {
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
        statement.execute("""
                CREATE TABLE IF NOT EXISTS ranks (
                    id INT,
                    rank VARCHAR(32) NOT NULL,
                    color INT NOT NULL,
                    emergencyOperator BOOLEAN NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE (rank)
                );""");
        statement.close();
    }

    public static void updateTableFromConfig(PoliceTerminalConfig config) throws SQLException {
        List<Integer> missingRankIds = selectMissingRankIdsFromTable(config);
        replaceIntoTable(config);
        Player.updateTableWithNewRankIds(config, missingRankIds);
        deleteMissingRankIdsFromTable(missingRankIds);
    }

    private static @NotNull List<Integer> selectMissingRankIdsFromTable(@NotNull PoliceTerminalConfig config)
            throws SQLException {
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
        StringBuilder query = new StringBuilder("SELECT id FROM ranks WHERE id NOT IN (");

        int size = config.ranks.size();
        for(int i = 0; i < size - 1; i++)
            query.append(config.ranks.get(i).id).append(", ");
        query.append(config.ranks.get(size - 1).id).append(");");

        ResultSet result = statement.executeQuery(query.toString());
        ArrayList<Integer> missingRankIds = new ArrayList<>();
        while(result.next())
            missingRankIds.add(result.getInt("id"));
        statement.close();

        return missingRankIds;
    }

    private static void replaceIntoTable(@NotNull PoliceTerminalConfig config) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                REPLACE INTO ranks
                (id, rank, emergencyOperator, color)
                VALUES (?, ?, ?, ?);""");
        for(Rank rank : config.ranks) {
            preparedStatement.setInt(1, rank.id);
            preparedStatement.setString(2, rank.rank);
            preparedStatement.setBoolean(3, rank.emergencyOperator);
            preparedStatement.setInt(4, rank.color);
            preparedStatement.execute();
        }
        preparedStatement.close();
    }

    private static void deleteMissingRankIdsFromTable(@NotNull List<Integer> missingRankIds) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                DELETE FROM ranks
                WHERE id=?;""");
        for(int rankId : missingRankIds) {
            preparedStatement.setInt(1, rankId);
            preparedStatement.execute();
        }
        preparedStatement.close();
    }

    public static void loadRanks() {
        ranks = new HashMap<>();
        for(Rank rank : RupyberDatabaseAPI.policeTerminalConfig.ranks) {
            ranks.put(rank.id, rank);
        }
    }

    public static Rank fromId(int id) {
        return ranks.get(id);
    }
}