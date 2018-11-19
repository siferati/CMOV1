package org.feup.cmov.validationcafeteria.util;

import android.app.Activity;
import android.widget.Toast;

public class Constants {

    /**
     * Shows toast message
     * @param toast - message to show
     */
    public static void showToast(final String toast, Activity activity) {
        activity.runOnUiThread(() -> Toast.makeText(activity, toast, Toast.LENGTH_LONG).show());
    }

    public static final int OK_RESPONSE = 200;
    public static final int NO_INTERNET = -1;
    public static final int SERVER_TIMEOUT = 5000;
    public static final String ERROR_CONNECTING = "Error connecting";

    public static final String FREE_POPCORN = "Popcorn";
    public static final String FREE_COFFEE = "Coffee";
    public static final String DISCOUNT = "Total";

    public static final String POPCORN_NAME = "Free Popcorn";
    public static final String COFFEE_NAME = "Free Coffee";
    public static final String DISCOUNT_NAME = "5% Discount";

    public static final String POPCORN_DESCRIPTION = "Get a free popcorn";
    public static final String COFFEE_DESCRIPTION = "Get a free coffee";
    public static final String DISCOUNT_DESCRIPTION = "5% discount on a order";

    public static final String SEND_ORDER = "send_order";
    public static final String SEE_VOUCHERS = "see_vouchers";
    public static final String NO_VOUCHERS = "No vouchers to show";
}
