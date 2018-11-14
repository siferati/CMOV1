package org.feup.cmov.customerapp.dataStructures;

import java.io.Serializable;

public class Ticket implements Serializable {
    private String id;
    private String name;
    private String date;
    private int seatNumber;
    private double price;
    private boolean used;

    public Ticket(String id, String name, String date, int seatNumber, double price) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.seatNumber = seatNumber;
        this.price = price;
        this.used = false;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public double getPrice() {
        return price;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
