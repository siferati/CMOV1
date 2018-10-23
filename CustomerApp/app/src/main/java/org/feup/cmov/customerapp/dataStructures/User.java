package org.feup.cmov.customerapp.dataStructures;

public final class User {
    public static String id;

    private static String name;
    private static String username;
    private static String password;
    private static String nifNumber;
    private static CreditCard creditCard;

    public static void setUser(String name, String username, String password, String nifNumber, CreditCard creditCard) {
        User.name = name;
        User.username = username;
        User.password = password;
        User.nifNumber = nifNumber;
        User.creditCard = creditCard;
    }

    public static void setId(String id) {
        User.id = id;
    }

    public static String getId() {
        return id;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        User.name = name;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        User.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        User.password = password;
    }

    public static String getNifNumber() {
        return nifNumber;
    }

    public static void setNifNumber(String nifNumber) {
        User.nifNumber = nifNumber;
    }

    public static CreditCard getCreditCard() {
        return creditCard;
    }

    public static void setCreditCard(CreditCard creditCard) {
        User.creditCard = creditCard;
    }
}
