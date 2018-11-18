package org.feup.cmov.validationcafeteria.dataStructures;

public class User {
    private String name;
    private String username;
    private String nif;

    public User() {}

    public User(String name, String username, String nif) {
        this.name = name;
        this.username = username;
        this.nif = nif;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getNif() {
        return nif;
    }
}
