package com.example.eventexplore_tfg.Data;

import java.io.Serializable;
/**
 * Represents a user with basic information.
 *
 * @version 1.0
 * @autor Pablo Esteban Martín
 */
public class User implements Serializable {
    private String id;
    private String username;
    private String email;
    private String role;


    /**
     * Constructor for creating a User object.
     *
     * @param id       User ID.
     * @param username User username.
     * @param role     User role.
     */
    public User(String id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
