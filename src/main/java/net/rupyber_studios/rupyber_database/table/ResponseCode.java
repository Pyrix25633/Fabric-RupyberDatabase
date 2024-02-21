package net.rupyber_studios.rupyber_database.table;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.rupyber_studios.rupyber_database.RupyberDatabase;
import net.rupyber_studios.rupyber_database.config.PoliceTerminalConfig;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class ResponseCode {
    @ConfigEntry.Gui.Excluded
    public static HashMap<Integer, ResponseCode> responseCodes;

    public int id = 10;
    public String code = "New Code";
    @ConfigEntry.ColorPicker
    public int color = 0xAAAAAA;
    public String description = "Description";

    public ResponseCode() {}

    public ResponseCode(int id, String code, int color, String description) {
        this.id = id;
        this.code = code;
        this.color = color;
        this.description = description;
    }

    public static void createTable() throws SQLException {
        Statement statement = RupyberDatabase.connection.createStatement();
        statement.execute("""
                CREATE TABLE IF NOT EXISTS responseCodes (
                    id INT,
                    code VARCHAR(16) NOT NULL,
                    color INT NOT NULL,
                    description VARCHAR(64) NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE (code)
                );""");
        statement.close();
    }

    public static void loadResponseCodes(@NotNull PoliceTerminalConfig config) {
        responseCodes = new HashMap<>();
        for(ResponseCode responseCode : config.responseCodes) {
            responseCodes.put(responseCode.id, responseCode);
        }
    }

    public static ResponseCode fromId(int id) {
        return responseCodes.get(id);
    }
}