package org.feup.cmov.validationevents.dataStructures;

import java.io.Serializable;

public class Ticket implements Serializable {
    private String ticketId;
    private String userid;
    private int showId;
    private String name;
    private String date;
    private int seatNumber;
    private double price;
    private boolean available;

    public Ticket(String id) {
        this.ticketId = id;
    }

    public Ticket(String id, String userid, int showId) {
        this.ticketId = id;
        this.userid = userid;
        this.showId = showId;
    }

    public Ticket(String id, int showId, String name, String date, int seatNumber, double price) {
        this.ticketId = id;
        this.showId = showId;
        this.name = name;
        this.date = date;
        this.seatNumber = seatNumber;
        this.price = price;
    }

    public String getUserid() {
        return userid;
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

    public String getTicketId() {
        return ticketId;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Ticket)) return false;
        Ticket t = (Ticket)other;

        if (t.getTicketId().equals(this.getTicketId()))
            return true;
        else return false;
    }
}
