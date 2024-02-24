package net.rupyber_studios.rupyber_database_api.test;

import com.github.javafaker.Faker;
import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;
import net.rupyber_studios.rupyber_database_api.config.PoliceTerminalConfig;
import net.rupyber_studios.rupyber_database_api.table.IncidentType;
import net.rupyber_studios.rupyber_database_api.table.Player;
import net.rupyber_studios.rupyber_database_api.table.Rank;
import net.rupyber_studios.rupyber_database_api.table.ResponseCode;
import net.rupyber_studios.rupyber_database_api.util.Callsign;
import net.rupyber_studios.rupyber_database_api.util.PlayerInfo;
import net.rupyber_studios.rupyber_database_api.util.Status;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class Seed {
    private static final int TEST_RECORDS = 10000;
    private static final Random RANDOM = new Random();
    private static final Faker FAKER = new Faker();
    private static final Map<UUID, Player> PLAYERS = new HashMap<>();

    public static void main(String[] args) throws IOException, SQLException {
        RupyberDatabaseAPI.connect(Path.of("./test/."));
        PoliceTerminalConfig config = loadPoliceTerminalConfig();
        RupyberDatabaseAPI.updatePoliceTerminalTablesFromConfig(config);
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                INSERT INTO players
                (uuid, username, online, status, rankId, callsign, callsignReserved)
                VALUES (?, ?, ? ,?, ?, ?, ?)""");
        RupyberDatabaseAPI.LOGGER.info("Started seeding...");
        long start = System.currentTimeMillis();
        for(int i = 0; i < TEST_RECORDS; i++)
            insertPlayer(preparedStatement);
        long end = System.currentTimeMillis();
        RupyberDatabaseAPI.LOGGER.info("SEED SUCCESSFUL in " + (end - start) + "ms");
        preparedStatement.close();
    }

    private static @NotNull PoliceTerminalConfig loadPoliceTerminalConfig() throws IOException {
        InputStream configInput = new FileInputStream("./test/police_terminal_config.json");
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
        RupyberDatabaseAPI.setPoliceTerminalConfig(config);
        return config;
    }

    private static void insertPlayer(@NotNull PreparedStatement preparedStatement) throws SQLException {
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
    }

    private static @NotNull Player fakePlayer() {
        PoliceTerminalConfig config = RupyberDatabaseAPI.policeTerminalConfig;
        // UUID
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while(PLAYERS.get(uuid) != null);
        // Username
        String username;
        boolean found;
        do {
            username = generateUsername();
            found = false;
            for(Player player : PLAYERS.values()) {
                if(player.username == null) continue;
                if(player.username.equals(username)) {
                    found = true;
                    break;
                }
            }
        } while(found);
        // Online
        boolean online = RANDOM.nextBoolean();
        // Status, rank and callsign
        if(RANDOM.nextBoolean()) {
            Rank rank = config.ranks.get(RANDOM.nextInt(config.ranks.size()));
            Status status = Status.values()[RANDOM.nextInt(Status.values().length)];
            String callsign = null;
            if(RANDOM.nextBoolean()) {
                do {
                    callsign = Callsign.createRandomCallsign();
                    found = false;
                    for(Player player : PLAYERS.values()) {
                        if(player.info == null) continue;
                        if(player.info.callsign == null) continue;
                        if(player.info.callsign.equals(callsign)) {
                            found = true;
                            break;
                        }
                    }
                } while(found);
            }
            Player player = new Player(uuid, username, online, new PlayerInfo(status, rank, callsign),
                    callsign != null && RANDOM.nextBoolean());
            PLAYERS.put(uuid, player);
            return player;
        }
        else {
            Player player = new Player(uuid, username, online, null, false);
            PLAYERS.put(uuid, player);
            return player;
        }
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