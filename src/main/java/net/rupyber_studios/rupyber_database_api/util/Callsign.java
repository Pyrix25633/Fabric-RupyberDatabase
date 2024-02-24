package net.rupyber_studios.rupyber_database_api.util;

import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;
import net.rupyber_studios.rupyber_database_api.config.PoliceTerminalConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Callsign {
    private static final Random RANDOM = new Random();
    private static final Pattern AREA_UNIT_BEAT_PATTERN = Pattern.compile("^(\\d+)-(\\w+)-(\\d+)$");
    private static final Pattern BEAT_UNIT_PATTERN = Pattern.compile("^(\\d+)-(\\w+)$");
    private static final Pattern UNIT_BEAT_PATTERN = Pattern.compile("^(\\w+)-(\\d+)$");

    public static @NotNull @Unmodifiable List<String> selectAll() throws SQLException {
        List<String> callsigns = new ArrayList<>();
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
        ResultSet result = statement.executeQuery("""
                SELECT callsign
                FROM players
                WHERE callsign IS NOT NULL;""");
        while (result.next())
            callsigns.add(result.getString("callsign"));
        statement.close();
        return callsigns;
    }

    public static String createUnusedCallsign() throws SQLException {
        String callsign;
        do {
            callsign = createRandomCallsign();
        } while(isInUse(callsign));
        return callsign;
    }

    public static String createRandomCallsign() {
        PoliceTerminalConfig config = RupyberDatabaseAPI.policeTerminalConfig;
        ArrayList<String> types = new ArrayList<>();
        if(config.callsignBeatUnit) types.add("B-U");
        if(config.callsignUnitBeat) types.add("U-B");
        if(config.callsignAreaUnitBeat || types.isEmpty()) types.add("A-U-B");
        short index = (short)(RANDOM.nextInt(0, types.size()));
        int area = RANDOM.nextInt(config.callsignAreaMin, config.callsignAreaMax + 1);
        short unitIndex = (short)(RANDOM.nextInt(0, config.callsignUnits.size()));
        int beat = RANDOM.nextInt(config.callsignBeatMin, config.callsignBeatMax + 1);
        return switch(types.get(index)) {
            case "B-U" -> beat + "-" + config.callsignUnits.get(unitIndex);
            case "U-B" -> config.callsignUnits.get(unitIndex) + "-" + beat;
            default -> area + "-" + config.callsignUnits.get(unitIndex) + "-" + beat;
        };
    }

    public static boolean isInUse(String callsign) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT uuid
                FROM players
                WHERE callsign=?;""");
        preparedStatement.setString(1, callsign);
        ResultSet result = preparedStatement.executeQuery();
        return result.next();
    }

    public static boolean isValid(String callsign) {
        PoliceTerminalConfig config = RupyberDatabaseAPI.policeTerminalConfig;
        Matcher matcher = AREA_UNIT_BEAT_PATTERN.matcher(callsign);
        if(matcher.find())
            return isAreaUnitBeatValid(matcher, config);

        matcher = BEAT_UNIT_PATTERN.matcher(callsign);
        if(matcher.find())
            return isBeatUnitValid(matcher, config);

        matcher = UNIT_BEAT_PATTERN.matcher(callsign);
        if(matcher.find())
            return isUnitBeatValid(matcher, config);

        return false;
    }

    private static boolean isAreaUnitBeatValid(@NotNull Matcher matcher, @NotNull PoliceTerminalConfig config) {
        int area = Integer.parseInt(matcher.group(1));
        if(area < config.callsignAreaMin || area > config.callsignAreaMax)
            return false;
        String unit = matcher.group(2);
        if(!config.callsignUnits.contains(unit))
            return false;
        int beat = Integer.parseInt(matcher.group(3));
        if(beat < config.callsignBeatMin || beat > config.callsignBeatMax)
            return false;
        return config.callsignAreaUnitBeat;
    }

    private static boolean isBeatUnitValid(@NotNull Matcher matcher, @NotNull PoliceTerminalConfig config) {
        int beat = Integer.parseInt(matcher.group(1));
        if(beat < config.callsignBeatMin || beat > config.callsignBeatMax)
            return false;
        String unit = matcher.group(2);
        if(!config.callsignUnits.contains(unit))
            return false;
        return config.callsignBeatUnit;
    }

    private static boolean isUnitBeatValid(@NotNull Matcher matcher, @NotNull PoliceTerminalConfig config) {
        String unit = matcher.group(1);
        if(!config.callsignUnits.contains(unit))
            return false;
        int beat = Integer.parseInt(matcher.group(2));
        if(beat < config.callsignBeatMin || beat > config.callsignBeatMax)
            return false;
        return config.callsignUnitBeat;
    }
}