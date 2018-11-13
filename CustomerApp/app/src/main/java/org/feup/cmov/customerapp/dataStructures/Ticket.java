package org.feup.cmov.customerapp.dataStructures;

public class Ticket {
    private int id;
    private String name;
    private String date;
    private int seatNumber;
    private double price;

    public Ticket(int id, String name, String date, int seatNumber, double price) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.seatNumber = seatNumber;
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

    public int getSeatNumber() {
        return seatNumber;
    }

    public double getPrice() {
        return price;
    }
}
