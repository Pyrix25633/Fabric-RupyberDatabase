package net.rupyber_studios.rupyber_database_api;

import net.fabricmc.api.ModInitializer;

import net.rupyber_studios.rupyber_database_api.config.PoliceTerminalConfig;
import net.rupyber_studios.rupyber_database_api.table.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RupyberDatabaseAPI implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("rupyber_database");

	public static Connection connection;

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing main");
	}

	public static void connectIfNotConnected(Path worldPath) throws SQLException {
		if(connection != null && !connection.isClosed()) return;
		connection = DriverManager.getConnection("jdbc:sqlite:" + worldPath + "rupyber.db");
	}

	public static void createPoliceTerminalTables() throws SQLException {
		Rank.createTable();
		Player.createTable();
		EmergencyCallNumber.createTable();
		EmergencyCall.createTable();
		ResponseCode.createTable();
		IncidentType.createTable();
		IncidentNumber.createTable();
		Incident.createTable();
		IncidentPlayer.createTable();
	}

	public static void updatePoliceTerminalTablesFromConfig(PoliceTerminalConfig config) throws SQLException {
		Rank.updateTableFromConfig(config);
		ResponseCode.updateTableFromConfig(config);
		IncidentType.updateTableFromConfig(config);
	}

	public static void handlePoliceTerminalShutdown() throws SQLException {
		Player.handleShutdown();
	}

	public static void createMinebuckCurrencyTables() {
		// TODO: transfer from Minebuck Currency
	}
}