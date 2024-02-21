package net.rupyber_studios.rupyber_database.table;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.rupyber_studios.rupyber_database.RupyberDatabase;
import net.rupyber_studios.rupyber_database.config.PoliceTerminalConfig;
import net.rupyber_studios.rupyber_database.util.Identifiable;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Rank extends Identifiable {
    @ConfigEntry.Gui.Excluded
    public static HashMap<Integer, Rank> ranks;

    public int id = 10;
    public String rank = "New Rank";
    @ConfigEntry.ColorPicker
    public int color = 0x5555FF;

    public Rank() {}

    public Rank(int id, String rank, int color) {
        this.id = id;
        this.rank = rank;
        this.color = color;
    }

    public static void createTable() throws SQLException {
        Statement statement = RupyberDatabase.connection.createStatement();
        statement.execute("""
                CREATE TABLE IF NOT EXISTS ranks (
                    id INT,
                    rank VARCHAR(32) NOT NULL,
                    color INT NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE (rank)
                );""");
        statement.close();
    }

    public static void updateTableFromConfig(PoliceTerminalConfig config) throws SQLException {
        // TODO: remove rankIds list and use the one in config
        List<Integer> rankIds = getRankIdsFromConfig(config);
        List<Integer> missingRankIds = selectMissingRankIdsFromTable(rankIds);
        replaceIntoTable();
        Player.updateTableWithNewRankIds(rankIds, missingRankIds);
        deleteMissingRankIdsFromTable(missingRankIds);
    }

    public static @NotNull List<Integer> getRankIdsFromConfig(@NotNull PoliceTerminalConfig config) throws IllegalStateException {
        List<Integer> rankIds = new ArrayList<>();
        for(Rank rank : config.ranks)
            rankIds.add(rank.id);
        if(rankIds.isEmpty()) throw new IllegalStateException("0 ranks, this is not possible");
        return rankIds;
    }

    public static @NotNull List<Integer> selectMissingRankIdsFromTable(@NotNull List<Integer> rankIds)
            throws SQLException {
        Statement statement = RupyberDatabase.connection.createStatement();
        StringBuilder query = new StringBuilder("SELECT id FROM ranks WHERE id NOT IN (");

        for(int i = 0; i < rankIds.size() - 1; i++)
            query.append(rankIds.get(i)).append(", ");
        query.append(rankIds.get(rankIds.size() - 1)).append(");");

        ResultSet result = statement.executeQuery(query.toString());
        ArrayList<Integer> missingRankIds = new ArrayList<>();
        while(result.next())
            missingRankIds.add(result.getInt("id"));

        return missingRankIds;
    }

    public static void replaceIntoTable() throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabase.connection.prepareStatement("""
                REPLACE INTO ranks
                (id, rank, color)
                VALUES (?, ?, ?);""");
        for(Rank rank : Rank.ranks.values()) {
            preparedStatement.setInt(1, rank.id);
            preparedStatement.setString(2, rank.rank);
            preparedStatement.setInt(3, rank.color);
            preparedStatement.execute();
        }
        preparedStatement.close();
    }

    public static void deleteMissingRankIdsFromTable(@NotNull List<Integer> missingRankIds) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabase.connection.prepareStatement("""
                DELETE FROM ranks
                WHERE id=?;""");
        for(int rankId : missingRankIds) {
            preparedStatement.setInt(1, rankId);
            preparedStatement.execute();
        }
    }

    public static void loadRanks(@NotNull PoliceTerminalConfig config) {
        ranks = new HashMap<>();
        for(Rank rank : config.ranks) {
            ranks.put(rank.id, rank);
        }
    }

    public static Rank fromId(int id) {
        return ranks.get(id);
    }
}