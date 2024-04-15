package net.rupyber_studios.rupyber_database_api.table;

import org.jooq.Record1;

import java.time.LocalDate;

import static net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI.context;
import static net.rupyber_studios.rupyber_database_api.jooq.Tables.*;

public interface EmergencyCallNumber {
    // -------------
    // Handle number
    // -------------

    static int getNewCallNumber(LocalDate currentDate) {
        int number = selectTodayNextUnusedNumber(currentDate);
        updateTodayNextNumber(currentDate, number);
        return number;
    }

    static int selectTodayNextUnusedNumber(LocalDate currentDate) {
        int number = selectTodayNextNumber(currentDate);
        Record1<Integer> result;
        do {
            number++;
            result = context.select(EmergencyCalls.id)
                    .from(EmergencyCalls)
                    .where(EmergencyCalls.closedAt.isNotNull().and(EmergencyCalls.callNumber.eq(number)))
                    .fetchOne();
        } while(result != null);
        return number;
    }

    static int selectTodayNextNumber(LocalDate currentDate) {
        Record1<Integer> number = context.select(EmergencyCallNumbers.number)
                .from(EmergencyCallNumbers)
                .where(EmergencyCallNumbers.day.eq(currentDate))
                .fetchOne();
        if(number != null) return number.value1();
        return 1;
    }

    static void updateTodayNextNumber(LocalDate currentDate, int number) {
        context.insertInto(EmergencyCallNumbers)
                .set(EmergencyCallNumbers.day, currentDate)
                .set(EmergencyCallNumbers.number, number)
                .onDuplicateKeyUpdate()
                .set(EmergencyCallNumbers.number, number)
                .execute();
    }

    // -------
    // Startup
    // -------

    static void createTable() {
        if(!context.meta().getTables().contains(EmergencyCallNumbers))
            context.ddl(EmergencyCallNumbers).executeBatch();
    }
}