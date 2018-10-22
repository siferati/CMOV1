package org.feup.cmov.customerapp.dataStructures;

import java.io.Serializable;

public class CreditCard implements Serializable {
    private CardType type;
    private String number;
    private int monthValidity;
    private int yearValidity;

    public CreditCard() { }

    public CreditCard(CardType type) {
        this.type = type;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getMonthValidity() {
        return monthValidity;
    }

    public void setMonthValidity(int monthValidity) {
        this.monthValidity = monthValidity;
    }

    public int getYearValidity() {
        return yearValidity;
    }

    public void setYearValidity(int yearValidity) {
        this.yearValidity = yearValidity;
    }
}
