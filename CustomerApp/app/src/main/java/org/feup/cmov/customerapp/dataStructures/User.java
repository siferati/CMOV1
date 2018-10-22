package org.feup.cmov.customerapp.dataStructures;

public class User {
    public static int id;

    private String name;
    private String username;
    private String password;
    private String nifNumber;
    private CreditCard creditCard;

    public User(int id, String name, String username, String password, String nifNumber, CreditCard creditCard) {
        User.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.nifNumber = nifNumber;
        this.creditCard = creditCard;
    }
}
