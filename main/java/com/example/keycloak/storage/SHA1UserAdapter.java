package com.example.keycloak.storage;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

public class SHA1UserAdapter extends AbstractUserAdapterFederatedStorage {
    
    private final String username;
    private final String email;
    
    public SHA1UserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, String username) {
        super(session, realm, model);
        this.username = username;
        this.email = username;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public void setUsername(String username) {
    }
    
    @Override
    public String getEmail() {
        return email;
    }
    
    @Override
    public void setEmail(String email) {
    }
    
    @Override
    public boolean isEmailVerified() {
        return true;
    }
    
    @Override
    public void setEmailVerified(boolean verified) {
    }
    
    @Override
    public String getFirstName() {
        return username;
    }
    
    @Override
    public void setFirstName(String firstName) {
    }
    
    @Override
    public String getLastName() {
        return "";
    }
    
    @Override
    public void setLastName(String lastName) {
    }
} 