package net.rupyber_studios.rupyber_database_api.util;

import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;
import net.rupyber_studios.rupyber_database_api.config.PoliceTerminalConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jooq.Record1;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI.context;
import static net.rupyber_studios.rupyber_database_api.jooq.Tables.*;

public class Callsign {
    private static final Random RANDOM = new Random();
    private static final Pattern AREA_UNIT_BEAT_PATTERN = Pattern.compile("^(\\d+)-(\\w+)-(\\d+)$");
    private static final Pattern BEAT_UNIT_PATTERN = Pattern.compile("^(\\d+)-(\\w+)$");
    private static final Pattern UNIT_BEAT_PATTERN = Pattern.compile("^(\\w+)-(\\d+)$");

    public static @NotNull @Unmodifiable List<String> selectAll() {
        Result<Record1<String>> result = context.select(Players.callsign)
                .from(Players)
                .where(Players.callsign.isNotNull())
                .fetch();
        List<String> callsigns = new ArrayList<>();
        for(Record1<String> record : result)
            callsigns.add(record.value1());
        return callsigns;
    }

    public static String createUnusedCallsign() {
        String callsign;
        do {
            callsign = createRandomCallsign();
        } while(isInUse(callsign));
        return callsign;
    }

    public static @NotNull String createRandomCallsign() {
        PoliceTerminalConfig config = RupyberDatabaseAPI.policeTerminalConfig;
        ArrayList<String> types = new ArrayList<>();
        if(config.callsignBeatUnit) types.add("B-U");
        if(config.callsignUnitBeat) types.add("U-B");
        if(config.callsignAreaUnitBeat || types.isEmpty()) types.add("A-U-B");
        short index = (short)(RANDOM.nextInt(0, types.size()));
        int area = RANDOM.nextInt(config.callsignAreaMin, config.callsignAreaMax + 1);
        short unitIndex = (short)(RANDOM.nextInt(0, config.callsignUnits.size()));
        int beat = RANDOM.nextInt(config.callsignBeatMin, config.callsignBeatMax + 1);
        return types.get(index)
                .replace("A", String.valueOf(area))
                .replace("U", config.callsignUnits.get(unitIndex))
                .replace("B", String.valueOf(beat));
    }

    public static boolean isInUse(String callsign) {
        return context.select(Players.uuid)
                .from(Players)
                .where(Players.callsign.eq(callsign)).fetchOne() != null;
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