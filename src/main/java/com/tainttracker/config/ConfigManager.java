package com.tainttracker.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static final String CONFIG_FILE = "config.properties";
    private static final String DB_URL = "jdbc:sqlite:tainttracker.sqlite";
    
    private Properties properties;
    private Connection dbConnection;

    private static ConfigManager instance;

    private ConfigManager() {
        properties = new Properties();
        loadConfig();
        initDatabase();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadConfig() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                properties.load(fis);
            } catch (IOException e) {
                logger.error("Failed to load config file", e);
            }
        }
    }

    public void saveConfig() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "TaintTracker Configuration");
        } catch (IOException e) {
            logger.error("Failed to save config file", e);
        }
    }

    private void initDatabase() {
        try {
            dbConnection = DriverManager.getConnection(DB_URL);
            try (Statement stmt = dbConnection.createStatement()) {
                // Create basic tables if not exist
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS projects (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        path TEXT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """);
            }
        } catch (SQLException e) {
            logger.error("Failed to initialize database", e);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
    
    public Connection getDbConnection() {
        return dbConnection;
    }
}
