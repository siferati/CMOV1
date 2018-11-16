package org.feup.cmov.customerapp.dataStructures;

import org.feup.cmov.customerapp.utils.Constants;

import java.io.Serializable;
import java.util.List;

public class Voucher implements Serializable {
    private String id;
    private String type;
    private double discount;
    private boolean available;
    private int quantity = 0;
    public boolean selected = false;

    public Voucher(String id, String type, double discount) {
        this.id = id;
        this.type = type;
        this.discount = discount;
        this.available = true;
    }

    public Voucher(String type, int quantity) {
        this.type = type;
        this.quantity = quantity;
        this.available = true;
    }

    public Voucher(String id) {
        this.id = id;
    }

    public static int getQuantityFromList(String type, List<Voucher> voucherList) {
        int quantity = 0;

        for (int i = 0; i < voucherList.size(); i++) {
            if (voucherList.get(i).getType().equals(type)) {
                quantity++;
            }
        }

        return quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName(String type) {
        if (type.equals(Constants.FREE_COFFEE)) {
            return Constants.COFFEE_NAME;
        } else if (type.equals(Constants.FREE_POPCORN)) {
            return Constants.POPCORN_NAME;
        } else {
            return Constants.DISCOUNT_NAME;
        }
    }

    public String getDescription(String type) {
        if (type.equals(Constants.FREE_COFFEE)) {
            return Constants.COFFEE_DESCRIPTION;
        } else if (type.equals(Constants.FREE_POPCORN)) {
            return Constants.POPCORN_DESCRIPTION;
        } else {
            return Constants.DISCOUNT_DESCRIPTION;
        }
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getDiscount() {
        return discount;
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
        Voucher v = (Voucher)other;

        if (v.getId().equals(this.getId()))
            return true;
        else return false;
    }
}
