package net.rupyber_studios.rupyber_database_api.table;

import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface EmergencyCallNumber {
    // -------------
    // Handle number
    // -------------

    static int getNewCallNumber(String currentDate) throws SQLException {
        int number = selectTodayNextUnusedNumber(currentDate);
        updateTodayNextNumber(currentDate, number);
        return number;
    }

    static int selectTodayNextUnusedNumber(String currentDate) throws SQLException {
        int number = selectTodayNextNumber(currentDate);
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT id
                FROM emergencyCalls
                WHERE closed=FALSE AND callNumber=?;""");
        ResultSet result;
        do {
            number++;
            preparedStatement.setInt(1, number);
            result = preparedStatement.executeQuery();
        } while(result.next());
        preparedStatement.close();
        return number;
    }

    static int selectTodayNextNumber(String currentDate) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                SELECT number
                FROM emergencyCallNumbers
                WHERE day=?;""");
        preparedStatement.setString(1, currentDate);
        ResultSet result = preparedStatement.executeQuery();
        if(result.next()) return result.getInt("number");
        preparedStatement.close();
        return 1;
    }

    static void updateTodayNextNumber(String currentDate, int number) throws SQLException {
        PreparedStatement preparedStatement = RupyberDatabaseAPI.connection.prepareStatement("""
                REPLACE INTO emergencyCallNumbers
                (day, number)
                VALUES (?, ?);""");
        preparedStatement.setString(1, currentDate);
        preparedStatement.setInt(2, number + 1);
        preparedStatement.execute();
        preparedStatement.close();
    }

    // -------
    // Startup
    // -------

    static void createTable() throws SQLException {
        Statement statement = RupyberDatabaseAPI.connection.createStatement();
        statement.execute("""
                CREATE TABLE IF NOT EXISTS emergencyCallNumbers (
                    day DATE,
                    number INT NOT NULL,
                    PRIMARY KEY (day)
                );""");
        statement.close();
    }
}