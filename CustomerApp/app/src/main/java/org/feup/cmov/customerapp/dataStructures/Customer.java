package org.feup.cmov.customerapp.dataStructures;

public class Customer {
    private String username;
    private String password;
    private String name;
    private String nifNumber;
    private CreditCard creditCard;

    public Customer() {
        creditCard = new CreditCard();
    }

    public void registerUser(String username, String password,
                             String name, String nifNumber,
                             CreditCard creditCard) {
        this.username = username;
        this.password = password;

        this.name = name;
        this.nifNumber = nifNumber;

        this.creditCard = creditCard;


        // TODO: register user on SERVER
    }

    public String getName() {
        return name;
    }

    public String getNifNumber() {
        return nifNumber;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }
}
