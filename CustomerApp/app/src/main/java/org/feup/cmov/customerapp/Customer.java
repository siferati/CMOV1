package org.feup.cmov.customerapp;

public class Customer {
    private String name;
    private int nifNumber;
    private CreditCard creditCard;

    public class CreditCard {
        public String type;
        public int number;
        public String validityDate;
    }

    public void registerUser(String name, int nifNumber, String ccType, int ccNumber, String ccValidityDate) {
        this.name = name;
        this.nifNumber = nifNumber;
        this.creditCard.type = ccType;
        this.creditCard.number = ccNumber;
        this.creditCard.validityDate = ccValidityDate;

        // TODO: register user on SERVER
    }

    public String getName() {
        return name;
    }

    public int getNifNumber() {
        return nifNumber;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }
}
