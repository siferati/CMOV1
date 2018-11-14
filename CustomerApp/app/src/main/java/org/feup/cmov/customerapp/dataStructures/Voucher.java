package org.feup.cmov.customerapp.dataStructures;

import org.feup.cmov.customerapp.utils.Constants;

public class Voucher {
    public enum VoucherType {
        FREE_POPCORN,
        FREE_COFFEE,
        DISCOUNT
    }

    private String id;
    private VoucherType type;
    private boolean available;

    public Voucher(String id, String type, boolean available) {
        this.id = id;
        this.type = getType(type);
        this.available = available;
    }

    public VoucherType getType(String type) {
        if (type.equals(Constants.FREE_POPCORN)) {
            return VoucherType.FREE_POPCORN;
        } else if (type.equals(Constants.FREE_COFFEE)) {
            return VoucherType.FREE_COFFEE;
        } else {
            return VoucherType.DISCOUNT;
        }
    }

    public String getId() {
        return id;
    }

    public VoucherType getType() {
        return type;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
