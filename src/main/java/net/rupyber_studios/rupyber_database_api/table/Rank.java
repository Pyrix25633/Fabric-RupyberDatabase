package net.rupyber_studios.rupyber_database_api.table;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;
import net.rupyber_studios.rupyber_database_api.config.PoliceTerminalConfig;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record1;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI.context;
import static net.rupyber_studios.rupyber_database_api.jooq.Tables.*;

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

    public static void createTable() {
        if(!context.meta().getTables().contains(Ranks))
            context.ddl(Ranks).executeBatch();
    }

    public static void updateTableFromConfig(PoliceTerminalConfig config) {
        Result<Record1<Integer>> missingRankIds = selectMissingRankIdsFromTable(config);
        replaceIntoTable(config);
        Player.updateTableWithNewRankIds(config, missingRankIds);
        deleteMissingRankIdsFromTable(missingRankIds);
    }

    private static @NotNull Result<Record1<Integer>> selectMissingRankIdsFromTable(@NotNull PoliceTerminalConfig config) {
        List<Integer> rankIds = new ArrayList<>();
        for(Rank rank : config.ranks)
            rankIds.add(rank.id);

        return context.select(Ranks.id)
                .from(Ranks)
                .where(Ranks.id.notIn(rankIds))
                .fetch();
    }

    private static void replaceIntoTable(@NotNull PoliceTerminalConfig config) {
        for(Rank rank : config.ranks) {
            context.insertInto(Ranks)
                    .set(Ranks.id, rank.id)
                    .set(Ranks.rank, rank.rank)
                    .set(Ranks.emergencyOperator, rank.emergencyOperator)
                    .set(Ranks.color, rank.color)
                    .onDuplicateKeyUpdate()
                    .set(Ranks.rank, rank.rank).set(Ranks.emergencyOperator, rank.emergencyOperator)
                    .set(Ranks.color, rank.color)
                    .execute();
        }
    }

    private static void deleteMissingRankIdsFromTable(@NotNull Result<Record1<Integer>> missingRankIds) {
        for(Record1<Integer> rankId : missingRankIds) {
            context.deleteFrom(Ranks)
                    .where(Ranks.id.eq(rankId.value1()))
                    .execute();
        }
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