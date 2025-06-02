package com.example.keycloak.storage;

import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.util.List;

@ApplicationScoped
@Named("sha1-database-user-storage")
public class SHA1UserStorageProviderFactory implements UserStorageProviderFactory<SHA1UserStorageProvider> {
    
    public static final String PROVIDER_NAME = "sha1-database-user-storage";
    
    @Override
    public SHA1UserStorageProvider create(KeycloakSession session, ComponentModel model) {
        return new SHA1UserStorageProvider(session, model);
    }
    
    @Override
    public String getId() {
        return PROVIDER_NAME;
    }
    
    @Override
    public String getHelpText() {
        return "SHA1 Database User Storage Provider - Validates users against external database with SHA1 password hashing";
    }
    
    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property("jdbcUrl", "JDBC URL", "Database JDBC URL", 
                         ProviderConfigProperty.STRING_TYPE, "", null)
                .property("dbUsername", "Database Username", "Database username for connection", 
                         ProviderConfigProperty.STRING_TYPE, "", null)
                .property("dbPassword", "Database Password", "Database password for connection", 
                         ProviderConfigProperty.PASSWORD, "", null)
                .property("driverClassName", "Driver Class Name", "JDBC driver class name", 
                         ProviderConfigProperty.STRING_TYPE, "com.mysql.cj.jdbc.Driver", null)
                .property("userQuery", "User Query", "SQL query to retrieve user (use ? for username parameter)", 
                         ProviderConfigProperty.STRING_TYPE, 
                         "SELECT username, password FROM users WHERE username = ?", null)
                .property("usernameColumn", "Username Column", "Name of the username column", 
                         ProviderConfigProperty.STRING_TYPE, "username", null)
                .property("passwordColumn", "Password Column", "Name of the password column (SHA1 hashed)", 
                         ProviderConfigProperty.STRING_TYPE, "password", null)
                .build();
    }
    
    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) 
            throws ComponentValidationException {
        
        if (isEmpty(config.get("jdbcUrl"))) {
            throw new ComponentValidationException("JDBC URL is required");
        }
        
        if (isEmpty(config.get("dbUsername"))) {
            throw new ComponentValidationException("Database username is required");
        }
        
        if (isEmpty(config.get("dbPassword"))) {
            throw new ComponentValidationException("Database password is required");
        }
        
        if (isEmpty(config.get("driverClassName"))) {
            throw new ComponentValidationException("Driver class name is required");
        }
        
        if (isEmpty(config.get("userQuery"))) {
            throw new ComponentValidationException("User query is required");
        }
        
        if (isEmpty(config.get("usernameColumn"))) {
            throw new ComponentValidationException("Username column is required");
        }
        
        if (isEmpty(config.get("passwordColumn"))) {
            throw new ComponentValidationException("Password column is required");
        }
    }
    
    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
    
    @Override
    public void onUpdate(KeycloakSession session, RealmModel realm, ComponentModel oldModel, ComponentModel newModel) {
        DatabaseConfig oldConfig = createDatabaseConfig(oldModel);
        DatabaseManager.closeDataSource(oldConfig);
    }
    
    @Override
    public void preRemove(KeycloakSession session, RealmModel realm, ComponentModel model) {
        DatabaseConfig config = createDatabaseConfig(model);
        DatabaseManager.closeDataSource(config);
    }
    
    private DatabaseConfig createDatabaseConfig(ComponentModel model) {
        DatabaseConfig config = new DatabaseConfig();
        config.setJdbcUrl(model.get("jdbcUrl"));
        config.setUsername(model.get("dbUsername"));
        config.setPassword(model.get("dbPassword"));
        config.setDriverClassName(model.get("driverClassName"));
        config.setUserQuery(model.get("userQuery"));
        config.setPasswordColumn(model.get("passwordColumn"));
        config.setUsernameColumn(model.get("usernameColumn"));
        return config;
    }
    
    @Override
    public int order() {
        return 0;
    }
} 