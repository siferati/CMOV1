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

    public static CardType getCardType(String type) {
        if (type.equals("Master Card")) {
            return MASTER_CARD;
        } else if (type.equals("Visa")) {
            return VISA;
        } else {
            return AMERICAN_EXPRESS;
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