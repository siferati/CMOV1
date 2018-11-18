package org.feup.cmov.customerapp.dataStructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Order implements Serializable {
    private int id;
    private String date;
    private double price;
    private ArrayList<Product> products;
    private ArrayList<Voucher> vouchers;

    public Order() {}

    public Order(int id, String date, double price, ArrayList<Product> products, ArrayList<Voucher> vouchers) {
        this.id = id;
        this.date = date;
        this.price = price;
        this.products = products;
        this.vouchers = vouchers;
    }

    public Order(int id, double price, ArrayList<Voucher> vouchers) {
        this.id = id;
        this.price = price;
        this.vouchers = vouchers;
    }

    public void deleteInvalidVouchers(ArrayList<Voucher> validVouchers) {
        List<Voucher> invalidVouchers = new ArrayList<>(vouchers);
        invalidVouchers.removeAll(validVouchers);

        if (invalidVouchers.size() > 0) {
            Iterator it = vouchers.iterator();

            while(it.hasNext())
            {
                Voucher v = (Voucher) it.next();
                if (invalidVouchers.contains(v)) {
                    it.remove();
                }
            }
        }
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

    public void setId(int id) {
        this.id = id;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ArrayList<Voucher> getVouchers() {
        return vouchers;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public void setVouchers(ArrayList<Voucher> vouchers) {
        this.vouchers = vouchers;
    }
}
