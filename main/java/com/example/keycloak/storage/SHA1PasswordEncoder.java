package com.example.keycloak.storage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SHA1PasswordEncoder {
    
    public static boolean verify(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        
        String cleanEncodedPassword = encodedPassword;
        if (encodedPassword.startsWith("{sha}") || encodedPassword.startsWith("{SHA}")) {
            cleanEncodedPassword = encodedPassword.substring(5);
        }
        
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] inputDigest = sha1.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            String inputBase64 = Base64.getEncoder().encodeToString(inputDigest);
            return inputBase64.equals(cleanEncodedPassword);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Error while hashing password", ex);
        }
    }
    
    public static String sha1Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] result = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(result);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not available", e);
        }
    }
} 