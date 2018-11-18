package org.feup.cmov.customerapp.dataStructures;

import java.io.Serializable;

public class Ticket implements Serializable {
    private String id;
    private int showId;
    private String name;
    private String date;
    private int seatNumber;
    private double price;
    private boolean available;
    public boolean selected;

    public Ticket(String id) {
        this.id = id;
    }

    public Ticket(String id, int showId, String name, String date, int seatNumber, double price) {
        this.id = id;
        this.showId = showId;
        this.name = name;
        this.date = date;
        this.seatNumber = seatNumber;
        this.price = price;
        this.available = true;
    }

    public String getId() {
        return id;
    }

    public int getShowId() {
        return showId;
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Voucher)) return false;
        Ticket t = (Ticket)other;

        if (t.getId().equals(this.getId()))
            return true;
        else return false;
    }
}
