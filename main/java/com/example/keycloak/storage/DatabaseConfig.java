package com.example.keycloak.storage;

public class DatabaseConfig {
    private String jdbcUrl;
    private String username;
    private String password;
    private String driverClassName;
    private String userQuery;
    private String passwordColumn;
    private String usernameColumn;
    
    public DatabaseConfig() {}
    
    public String getJdbcUrl() {
        return jdbcUrl;
    }
    
    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getDriverClassName() {
        return driverClassName;
    }
    
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }
    
    public String getUserQuery() {
        return userQuery;
    }
    
    public void setUserQuery(String userQuery) {
        this.userQuery = userQuery;
    }
    
    public String getPasswordColumn() {
        return passwordColumn;
    }
    
    public void setPasswordColumn(String passwordColumn) {
        this.passwordColumn = passwordColumn;
    }
    
    public String getUsernameColumn() {
        return usernameColumn;
    }
    
    public void setUsernameColumn(String usernameColumn) {
        this.usernameColumn = usernameColumn;
    }
} 