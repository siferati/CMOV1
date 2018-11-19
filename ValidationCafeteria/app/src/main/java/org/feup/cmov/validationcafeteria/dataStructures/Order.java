package org.feup.cmov.validationcafeteria.dataStructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Order implements Serializable {
    private int orderId = -1;
    private String userId;
    private double price;
    private ArrayList<Product> products;
    private ArrayList<Voucher> vouchers;
    private String signature;
    private String jsonString;

    public Order() {}

    public Order(String userId, String signature, String jsonString, ArrayList<Product> products, ArrayList<Voucher> vouchers) {
        this.userId = userId;
        this.signature = signature;
        this.jsonString = jsonString;
        this.products = products;
        this.vouchers = vouchers;
    }

    public Order(int orderId, double price, ArrayList<Voucher> vouchersList) {
        this.orderId = orderId;
        this.price = price;
        this.vouchers = vouchersList;
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

    public String getSignature() {
        return signature;
    }

    public String getJsonString() {
        return jsonString;
    }
}
