package org.feup.cmov.customerapp.dataStructures;

public class Show {
    private String name;
    private String date;

    public Show(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
}
