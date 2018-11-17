package org.feup.cmov.customerapp.dataStructures;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {
    private int id;
    private String date;
    private double price;
    private ArrayList<Product> products;

    Order(int id, String date, double price, ArrayList<Product> products) {
        this.id = id;
        this.date = date;
        this.price = price;
        this.products = products;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }
}
