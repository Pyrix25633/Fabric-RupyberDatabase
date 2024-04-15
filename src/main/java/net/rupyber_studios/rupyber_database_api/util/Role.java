package net.rupyber_studios.rupyber_database_api.util;

import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI.context;
import static net.rupyber_studios.rupyber_database_api.jooq.Tables.Roles;

public enum Role implements StringIdentifiable {
    OFFICER, SUSPECT, VICTIM;

    @Contract(pure = true)
    public static @Nullable Role fromId(int id) {
        if(id == 0) return null;
        return values()[id - 1];
    }

    @Contract(value = " -> new", pure = true)
    public @NotNull Text getText() {
        return Text.translatable("text.hud.police_terminal.role." + this.name().toLowerCase());
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
            case OFFICER -> new Pair<>("Officer", 0x5555FF);
            case SUSPECT -> new Pair<>("Suspect", 0xAA00AA);
            case VICTIM -> new Pair<>("Victim", 0x00AA00);
        };
    }

    // -------
    // Startup
    // -------

    public static void createTable() {
        if(!context.meta().getTables().contains(Roles))
            context.ddl(Roles).executeBatch();
        for(Role role : values()) {
            Pair<String, Integer> data = role.getData();
            context.insertInto(Roles)
                    .values(role.getId(), data.getLeft(), data.getRight())
                    .onDuplicateKeyIgnore()
                    .execute();
        }
    }
}