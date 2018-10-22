package org.feup.cmov.customerapp.dataStructures;

import java.io.Serializable;

public enum CardType implements Serializable {
    MASTER_CARD(1),
    VISA(2),
    AMERICAN_EXPRESS(3);

    private final int cardID;

    CardType(int id) {
        this.cardID = id;
    }

    CardType(String type) {
        if (type.equals("Master Card")) {
            this.cardID = 1;
        } else if (type.equals("Visa")) {
            this.cardID = 2;
        } else {
            this.cardID = 3;
        }
    }

    @Override
    public String toString() {
        if (cardID == 1) {
            return "Master Card";
        } else if (cardID == 2) {
            return "Visa";
        } else {
            return "American Express";
        }
    }
}