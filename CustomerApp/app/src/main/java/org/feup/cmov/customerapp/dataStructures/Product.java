package org.feup.cmov.customerapp.dataStructures;

import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String name;
    private String image;
    private double price;
    private int quantity = 0;

    public Product(int id, String name, double price, String image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public Product(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public double getTotalPriceRounded() {
        double totalPrice = (double)quantity * price;

        double rounded = (double) Math.round(totalPrice * 100.0) / 100.0;
        return rounded;
    }

    public double getTotalPrice() {
        return (double)quantity * price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public double getPrice() {
        return price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }
}
