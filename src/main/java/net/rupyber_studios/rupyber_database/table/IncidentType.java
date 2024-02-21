package net.rupyber_studios.rupyber_database.table;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.rupyber_studios.rupyber_database.RupyberDatabase;
import net.rupyber_studios.rupyber_database.config.PoliceTerminalConfig;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class IncidentType {
    @ConfigEntry.Gui.Excluded
    public static HashMap<Integer, IncidentType> incidentTypes;

    public int id = 10;
    public String code = "New Code";
    @ConfigEntry.ColorPicker
    public int color = 0x55FF55;
    public String description = "Description";

    public IncidentType() {}

    public IncidentType(int id, String code, int color, String description) {
        this.id = id;
        this.code = code;
        this.color = color;
        this.description = description;
    }

    public static void createTable() throws SQLException {
        Statement statement = RupyberDatabase.connection.createStatement();
        statement.execute("""
                CREATE TABLE IF NOT EXISTS incidentTypes (
                    id INT,
                    code VARCHAR(8) NOT NULL,
                    color INT NOT NULL,
                    description VARCHAR(64) NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE (code)
                );""");
        statement.close();
    }

    public static void loadIncidentTypes(@NotNull PoliceTerminalConfig config) {
        incidentTypes = new HashMap<>();
        for(IncidentType incidentType : config.incidentTypes) {
            incidentTypes.put(incidentType.id, incidentType);
        }
    }

    public static IncidentType fromId(int id) {
        return incidentTypes.get(id);
    }
}