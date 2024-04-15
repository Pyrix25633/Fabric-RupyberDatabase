package net.rupyber_studios.rupyber_database_api;

import net.fabricmc.api.ModInitializer;

import net.rupyber_studios.rupyber_database_api.config.PoliceTerminalConfig;
import net.rupyber_studios.rupyber_database_api.table.*;
import net.rupyber_studios.rupyber_database_api.util.Priority;
import net.rupyber_studios.rupyber_database_api.util.Recipients;
import net.rupyber_studios.rupyber_database_api.util.Role;
import net.rupyber_studios.rupyber_database_api.util.Status;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RupyberDatabaseAPI implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("rupyber_database");

	public static Connection connection;
	public static DSLContext context;
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
				context = DSL.using(connection, SQLDialect.SQLITE);
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
				context = null;
				LOGGER.info("Disconnected from database");
			}
		} catch(SQLException e) {
			LOGGER.error("Error while disconnecting from database: ", e);
			throw new IllegalStateException("Disconnection from database failed");
		}
	}

	public static void createPoliceTerminalTables() {
		Rank.createTable();
		Status.createTable();
		Player.createTable();
		EmergencyCallNumber.createTable();
		EmergencyCall.createTable();
		ResponseCode.createTable();
		Priority.createTable();
		Recipients.createTable();
		IncidentType.createTable();
		IncidentNumber.createTable();
		Incident.createTable();
		Role.createTable();
		IncidentPlayer.createTable();
	}

	public static void updatePoliceTerminalTablesFromConfig() {
		PoliceTerminalConfig config = policeTerminalConfig;
		Rank.updateTableFromConfig(config);
		ResponseCode.updateTableFromConfig(config);
		IncidentType.updateTableFromConfig(config);
	}

	public static void handlePoliceTerminalShutdown() {
		Player.handleShutdown();
	}

	public static void createMinebuckCurrencyTables() {
		// TODO: transfer from Minebuck Currency
	}
}