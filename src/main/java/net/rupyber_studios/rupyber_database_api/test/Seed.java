package net.rupyber_studios.rupyber_database_api.test;

import com.github.javafaker.Faker;
import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;
import net.rupyber_studios.rupyber_database_api.config.PoliceTerminalConfig;
import net.rupyber_studios.rupyber_database_api.table.Player;
import net.rupyber_studios.rupyber_database_api.table.Rank;
import net.rupyber_studios.rupyber_database_api.util.Callsign;
import net.rupyber_studios.rupyber_database_api.util.PlayerInfo;
import net.rupyber_studios.rupyber_database_api.util.Status;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class Seed {
    private static final int TEST_RECORDS = 1000000;
    private static final Random RANDOM = new Random();
    private static final Faker FAKER = new Faker();

    public static void main(String[] args) throws IOException, SQLException {
        RupyberDatabaseAPI.connectIfNotConnected(Path.of("./test/."));
        PoliceTerminalConfig.load("./test/police_terminal_config.json");
        RupyberDatabaseAPI.updatePoliceTerminalTablesFromConfig();
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                INSERT INTO players
                (uuid, username, online, status, rankId, callsign, callsignReserved)
                VALUES (?, ?, ? ,?, ?, ?, ?)""");
        RupyberDatabaseAPI.LOGGER.info("Started seeding...");
        long start = System.currentTimeMillis();
        for(int i = 0; i < TEST_RECORDS; i++) {
            insertPlayer(preparedStatement);
            double progress = ((double) i / TEST_RECORDS) * 100;
            if(progress % 5 == 0)
                RupyberDatabaseAPI.LOGGER.info("Seeding... " + progress + "%");
        }
        long end = System.currentTimeMillis();
        RupyberDatabaseAPI.LOGGER.info("SEED SUCCESSFUL in " + (end - start) + "ms");
        preparedStatement.close();
    }

    private static void insertPlayer(@NotNull PreparedStatement preparedStatement) {
        boolean validUser;
        do {
            try {
                Player player = fakePlayer();
                preparedStatement.setString(1, player.uuid.toString());
                preparedStatement.setString(2, player.username);
                preparedStatement.setBoolean(3, player.online);
                if(player.info != null) {
                    preparedStatement.setInt(4, player.info.status.getId());
                    preparedStatement.setInt(5, player.info.rank.id);
                    preparedStatement.setString(6, player.info.callsign);
                }
                else {
                    preparedStatement.setObject(4, null);
                    preparedStatement.setObject(5, null);
                    preparedStatement.setObject(6, null);
                }
                preparedStatement.setBoolean(7, player.callsignReserved);
                preparedStatement.execute();
                validUser = true;
            } catch(SQLException ignored) {
                validUser = false;
            }
        } while (!validUser);
        RupyberDatabaseAPI.disconnectIfConnected();
    }

    private static @NotNull Player fakePlayer() {
        PoliceTerminalConfig config = RupyberDatabaseAPI.policeTerminalConfig;
        UUID uuid = UUID.randomUUID();
        String username = generateUsername();
        boolean online = RANDOM.nextBoolean();
        if(RANDOM.nextBoolean()) {
            Rank rank = config.ranks.get(RANDOM.nextInt(config.ranks.size()));
            Status status = Status.values()[RANDOM.nextInt(Status.values().length)];
            String callsign = null;
            if(RANDOM.nextBoolean()) {
                callsign = Callsign.createRandomCallsign();
            }
            return new Player(uuid, username, online, new PlayerInfo(status, rank, callsign),
                    callsign != null && RANDOM.nextBoolean());
        }
        else
            return new Player(uuid, username, online, null, false);
    }

    private static @Nullable String generateUsername() {
        if(RANDOM.nextInt(70) == 0) return null;
        String username;
        String prefix = FAKER.superhero().prefix();
        String name = FAKER.name().firstName();
        String number = FAKER.address().buildingNumber();
        if(RANDOM.nextBoolean()) // Both
            username = prefix + name;
        else if(RANDOM.nextBoolean()) // Only prefix
            username = prefix;
        else // Only name
            username = name;
        if(RANDOM.nextBoolean()) // Number
            username += number;
        if(username.length() > 16) return generateUsername();
        return username;
    }
}