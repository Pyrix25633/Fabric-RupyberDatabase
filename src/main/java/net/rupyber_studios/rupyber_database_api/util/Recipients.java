package net.rupyber_studios.rupyber_database_api.util;

import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI.context;
import static net.rupyber_studios.rupyber_database_api.jooq.Tables.Recipients;

public enum Recipients implements StringIdentifiable {
    NEARBY, AVAILABLE, ALL;

    @Contract(pure = true)
    public static @Nullable Recipients fromId(int id) {
        if(id == 0) return null;
        return values()[id - 1];
    }

    @Contract(value = " -> new", pure = true)
    public @NotNull Text getText() {
        return Text.translatable("text.hud.police_terminal.recipients." + this.name().toLowerCase());
    }

    public int getId() {
        return this.ordinal() + 1;
    }

    @Override
    public @NotNull String asString() {
        return this.name().toLowerCase();
    }

    public @NotNull Pair<String, Integer> getData() {
        return switch(this) {
            case NEARBY -> new Pair<>("Nearby", 0xFFFF55);
            case AVAILABLE -> new Pair<>("Available", 0x55FF55);
            case ALL -> new Pair<>("All", 0xFF5555);
        };
    }

    // -------
    // Startup
    // -------

    public static void createTable() {
        if(!context.meta().getTables().contains(Recipients))
            context.ddl(Recipients).executeBatch();
        for(Recipients recipients : values()) {
            Pair<String, Integer> data = recipients.getData();
            context.insertInto(Recipients)
                    .values(recipients.getId(), data.getLeft(), data.getRight())
                    .onDuplicateKeyIgnore()
                    .execute();
        }
    }
}