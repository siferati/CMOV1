package org.feup.cmov.validationcafeteria.dataStructures;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {
    private int orderId = -1;
    private String userId;
    private double price;
    private ArrayList<Product> products;
    private ArrayList<Voucher> vouchers;

    public Order() {}

    public Order(int orderId, String userId, double price, ArrayList<Product> products, ArrayList<Voucher> vouchers) {
        this.orderId = orderId;
        this.userId = userId;
        this.price = price;
        this.products = products;
        this.vouchers = vouchers;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ArrayList<Product> getProducts() {
        return products;
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
