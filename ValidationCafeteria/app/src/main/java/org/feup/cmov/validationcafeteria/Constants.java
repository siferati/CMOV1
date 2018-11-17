package org.feup.cmov.validationcafeteria;

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

    public static final String SEE_VOUCHERS = "see_vouchers";
    public static final String NO_VOUCHERS = "No vouchers to show";

    public static final String FREE_POPCORN = "Popcorn";
    public static final String FREE_COFFEE = "Coffee";
    public static final String DISCOUNT = "Discount";

    public static final String POPCORN_NAME = "Free Popcorn";
    public static final String COFFEE_NAME = "Free Coffee";
    public static final String DISCOUNT_NAME = "5% Discount";

    public static final String POPCORN_DESCRIPTION = "Get a free popcorn";
    public static final String COFFEE_DESCRIPTION = "Get a free coffee";
    public static final String DISCOUNT_DESCRIPTION = "5% discount on a cafeteria order";
}
