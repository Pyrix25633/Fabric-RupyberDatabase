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
	public static final Logger LOGGER = LoggerFactory.getLogger("rupyber_database");

	public static Connection connection;
	public static PoliceTerminalConfig policeTerminalConfig = null;

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing main");
	}

	public static void setPoliceTerminalConfig(PoliceTerminalConfig config) {
		policeTerminalConfig = config;
	}

	public static void connectIfNotConnected(Path worldPath) {
		try {
			if(connection == null || connection.isClosed()) {
				connection = DriverManager.getConnection("jdbc:sqlite:" + worldPath + "rupyber.db");
				LOGGER.info("Connected to database");
			}
			else {
				LOGGER.info("Already connected to database");
			}
		} catch(SQLException e) {
			LOGGER.error("Error while connecting to database: ", e);
			throw new IllegalStateException("Connection to database failed");
		}
	}

	public static void startPoliceTerminal() {
		Rank.loadRanks();
		ResponseCode.loadResponseCodes();
		IncidentType.loadIncidentTypes();
		createPoliceTerminalTables();
		updatePoliceTerminalTablesFromConfig();
		handlePoliceTerminalShutdown();
	}

	public static void stopPoliceTerminal() {
		handlePoliceTerminalShutdown();
	}

	public static void disconnectIfConnected() {
		try {
			if(connection == null || connection.isClosed()) {
				LOGGER.info("Already disconnected from database");
			}
			else {
				connection.close();
				connection = null;
				LOGGER.info("Disconnected from database");
			}
		} catch(SQLException e) {
			LOGGER.error("Error while disconnecting from database: ", e);
			throw new IllegalStateException("Disconnection from database failed");
		}
	}

	public static void createPoliceTerminalTables() {
		try {
			Rank.createTable();
			Player.createTable();
			EmergencyCallNumber.createTable();
			EmergencyCall.createTable();
			ResponseCode.createTable();
			IncidentType.createTable();
			IncidentNumber.createTable();
			Incident.createTable();
			IncidentPlayer.createTable();
		} catch (SQLException e) {
			LOGGER.error("Error while creating Police Terminal tables: ", e);
			throw new IllegalStateException("Unable to create Police Terminal tables");
		}
	}

	public static void updatePoliceTerminalTablesFromConfig() {
		PoliceTerminalConfig config = policeTerminalConfig;
		try {
			Rank.updateTableFromConfig(config);
			ResponseCode.updateTableFromConfig(config);
			IncidentType.updateTableFromConfig(config);
		} catch(SQLException e) {
			LOGGER.error("Error while updating Police Terminal tables: ", e);
			throw new IllegalStateException("Unable to update Police Terminal tables");
		}
	}

	public static void handlePoliceTerminalShutdown() {
		try {
			Player.handleShutdown();
		} catch(SQLException e) {
			LOGGER.error("Error while handling Police Terminal shutdown: ", e);
			throw new IllegalStateException("Unable to handle Police Terminal shutdown");
		}
	}

	public static void createMinebuckCurrencyTables() {
		// TODO: transfer from Minebuck Currency
	}
}