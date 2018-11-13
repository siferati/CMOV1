package org.feup.cmov.customerapp.dataStructures;

import java.io.Serializable;

public class Show implements Serializable {
    private int id;
    private String name;
    private String description;
    private String date;
    private double price;

    public Show() {}

    public Show(int id, String name, String description, String date, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }
}
