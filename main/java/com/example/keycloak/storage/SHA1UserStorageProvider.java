package com.example.keycloak.storage;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class SHA1UserStorageProvider implements UserStorageProvider, UserLookupProvider, UserQueryProvider, CredentialInputValidator {
    
    private static final Logger logger = Logger.getLogger(SHA1UserStorageProvider.class);
    
    private final KeycloakSession session;
    private final ComponentModel model;
    private final DatabaseConfig config;
    
    public SHA1UserStorageProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.model = model;
        this.config = createDatabaseConfig(model);
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
    public UserModel getUserById(RealmModel realm, String id) {
        logger.debugf("getUserById: %s", id);
        String externalId = StorageId.externalId(id);
        return getUserByUsername(realm, externalId);
    }
    
    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        logger.debugf("getUserByUsername: %s", username);
        DatabaseManager.UserCredentials userCredentials = DatabaseManager.getUserCredentials(config, username);
        if (userCredentials != null) {
            return new SHA1UserAdapter(session, realm, model, userCredentials.getUsername());
        }
        return null;
    }
    
    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        logger.debugf("getUserByEmail: %s", email);
        return getUserByUsername(realm, email);
    }
    
    @Override
    public boolean supportsCredentialType(String credentialType) {
        return CredentialModel.PASSWORD.equals(credentialType);
    }
    
    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType);
    }
    
    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        if (!supportsCredentialType(credentialInput.getType())) {
            return false;
        }
        
        String username = user.getUsername();
        String password = credentialInput.getChallengeResponse();
        
        logger.debugf("Validating password for user: %s", username);
        
        DatabaseManager.UserCredentials userCredentials = DatabaseManager.getUserCredentials(config, username);
        if (userCredentials != null) {
            boolean isValid = SHA1PasswordEncoder.verify(password, userCredentials.getPassword());
            logger.debugf("Password validation result for user %s: %s", username, isValid);
            return isValid;
        }
        
        logger.debugf("User not found: %s", username);
        return false;
    }
    
    @Override
    public int getUsersCount(RealmModel realm) {
        return 0;
    }
    
    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult, Integer maxResults) {
        return Stream.empty();
    }
    
    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, org.keycloak.models.GroupModel group, Integer firstResult, Integer maxResults) {
        return Stream.empty();
    }
    
    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realm, String attrName, String attrValue) {
        return Stream.empty();
    }
    
    @Override
    public Set<String> getSupportedCredentialTypes() {
        return Collections.singleton(CredentialModel.PASSWORD);
    }
    
    @Override
    public void close() {
        DatabaseManager.closeDataSource(config);
    }
} 