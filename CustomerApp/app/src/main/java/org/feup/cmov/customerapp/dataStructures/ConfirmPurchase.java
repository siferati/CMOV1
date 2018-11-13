package org.feup.cmov.customerapp.dataStructures;

import java.io.Serializable;

public class ConfirmPurchase implements Serializable {
    public String name;
    public int quantity;
    public double price;

    public ConfirmPurchase(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public double getTotalPrice() {
        double totalPrice = (double)quantity * price;

        double rounded = (double) Math.round(totalPrice * 100.0) / 100.0;
        return rounded;
    }
}
