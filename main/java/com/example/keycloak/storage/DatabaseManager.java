package com.example.keycloak.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jboss.logging.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseManager {
    private static final Logger logger = Logger.getLogger(DatabaseManager.class);
    private static final ConcurrentHashMap<String, HikariDataSource> dataSources = new ConcurrentHashMap<>();
    
    public static DataSource getDataSource(DatabaseConfig config) {
        String key = generateKey(config);
        return dataSources.computeIfAbsent(key, k -> createDataSource(config));
    }
    
    private static String generateKey(DatabaseConfig config) {
        return config.getJdbcUrl() + "_" + config.getUsername();
    }
    
    private static HikariDataSource createDataSource(DatabaseConfig config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getJdbcUrl());
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(config.getPassword());
        hikariConfig.setDriverClassName(config.getDriverClassName());
        
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setIdleTimeout(600000);
        hikariConfig.setMaxLifetime(1800000);
        
        return new HikariDataSource(hikariConfig);
    }
    
    public static UserCredentials getUserCredentials(DatabaseConfig config, String username) {
        DataSource dataSource = getDataSource(config);
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(config.getUserQuery())) {
            
            statement.setString(1, username);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString(config.getPasswordColumn());
                    String storedUsername = resultSet.getString(config.getUsernameColumn());
                    return new UserCredentials(storedUsername, storedPassword);
                }
            }
        } catch (SQLException e) {
            logger.error("Error querying user credentials for username: " + username, e);
        }
        
        return null;
    }
    
    public static void closeDataSource(DatabaseConfig config) {
        String key = generateKey(config);
        HikariDataSource dataSource = dataSources.remove(key);
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
    
    public static class UserCredentials {
        private final String username;
        private final String password;
        
        public UserCredentials(String username, String password) {
            this.username = username;
            this.password = password;
        }
        
        public String getUsername() {
            return username;
        }
        
        public String getPassword() {
            return password;
        }
    }
} 