package org.feup.cmov.customerapp;

public class Customer {
    private String username;
    private String password;
    private String name;
    private String nifNumber;
    private CreditCard creditCard;

    public Customer() {}

    public class CreditCard {
        public String type;
        public String number;
        public String validityDate;

        public CreditCard() {}
    }

    public void registerUser(String username, String password,
                             String name, String nifNumber,
                             String ccType, String ccNumber, String ccValidityDate) {
        this.username = username;
        this.password = password;

        this.name = name;
        this.nifNumber = nifNumber;

        this.creditCard = new CreditCard();

        this.creditCard.type = ccType;
        this.creditCard.number = ccNumber;
        this.creditCard.validityDate = ccValidityDate;

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
