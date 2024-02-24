package net.rupyber_studios.rupyber_database_api;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.util.WorldSavePath;
import net.rupyber_studios.rupyber_database_api.config.PoliceTerminalConfig;
import net.rupyber_studios.rupyber_database_api.table.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

public class RupyberDatabaseAPI implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("rupyber_database");

	public static Connection connection;
	public static boolean policeTerminalInitialized = false;
	public static PoliceTerminalConfig policeTerminalConfig = null;
	public static Semaphore initialized = new Semaphore(0);

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
			Path worldPath = server.getSavePath(WorldSavePath.ROOT);
			connect(worldPath);
			if(policeTerminalInitialized) {
				Rank.loadRanks(policeTerminalConfig);
				ResponseCode.loadResponseCodes(policeTerminalConfig);
				IncidentType.loadIncidentTypes(policeTerminalConfig);
				createPoliceTerminalTables();
				updatePoliceTerminalTablesFromConfig(policeTerminalConfig);
				handlePoliceTerminalShutdown();
			}
			initialized.release();
		});

		ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
			if(policeTerminalInitialized)
				handlePoliceTerminalShutdown();
			disconnect();
		});

		LOGGER.info("Initializing main");
	}

	public static void setPoliceTerminalConfig(PoliceTerminalConfig config) {
		policeTerminalInitialized = true;
		policeTerminalConfig = config;
	}

	public static void connect(Path worldPath) {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + worldPath + "rupyber.db");
			LOGGER.info("Connected to database");
		} catch(SQLException e) {
			LOGGER.error("Error while connecting to database: ", e);
			throw new IllegalStateException("Connection to database failed");
		}
	}

	public static void disconnect() {
		try {
			connection.close();
			connection = null;
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

	public static void updatePoliceTerminalTablesFromConfig(PoliceTerminalConfig config) {
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