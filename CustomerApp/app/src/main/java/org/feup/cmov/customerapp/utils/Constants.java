package org.feup.cmov.customerapp.utils;

import android.app.Activity;
import android.widget.Toast;

import org.feup.cmov.customerapp.login.LoginActivity;

public class Constants {
    public static final String SHARED_PREF = "shared_preferences";
    public static final String PREF_PRODUCTS = "products";

    public static final int OK_RESPONSE = 200;
    public static final int NO_INTERNET = -1;
    public static final int SERVER_TIMEOUT = 2000;

    public static final String FREE_POPCORN = "Popcorn";
    public static final String FREE_COFFEE = "Coffee";
    public static final String DISCOUNT = "Discount";

    public static final double DEFAULT_DISCOUNT = 0.05;

    public static final String POPCORN_DESCRIPTION = "Get a free popcorn";
    public static final String COFFEE_DESCRIPTION = "Get a free coffee";
    public static final String DISCOUNT_DESCRIPTION = "5% discount on a cafeteria order";

    public static final String POPCORN_NAME = "Free Popcorn";
    public static final String COFFEE_NAME = "Free Coffee";
    public static final String DISCOUNT_NAME = "5% Discount";

    public static final int SHOWS_PER_LOAD = 10;

    public static final String ERROR_CONNECTING = "Error connecting";
    public static final String BUYING_TICKETS = "Buying Tickets...";

    public static final String LOGIN_SUCCESS = "Login Success";
    public static final String LOGOUT_SUCCESS = "Logout Success";
    public static final String REGISTER_SUCCESS = "Register Success";

    public static final String LOGIN_FAILED = "Login Failed";
    public static final String REGISTER_FAILED = "Register Failed";

    public static final String CREDIT_CARD = "creditCard";
    public static final String GET_SHOW = "get_show";
    public static final String CONFIRM_PURCHASE = "confirm_purchase";
    public static final String VALIDATE_TICKETS = "validate_tickets";
    public static final String VALIDATION_QR = "validation_qr";
    public static final String REQUESTED_VOUCHERS = "vouchers";
    public static final String SELECTED_VOUCHERS = "selected_vouchers";
    public static final String ORDER_VALIDATION = "order_validation";
    public static final String VOUCHERS_VALIDATION = "vouchers_validation";

    public static final String BUY_FAILED = "Need to buy at least a ticket";
    public static final String DECREASE_FAILED = "Negative number of tickets not allowed";

    public static final String VALIDATE_FAILED = "Can't validate more than 4 tickets at once";
    public static final String NO_TICKETS = "Need at least 1 ticket to validate";
    public static final String SINGLE_SHOW = "Tickets to validate need to be from the same show";

    public static final String LOCAL_LOGIN = "Local Login";
    public static final String INVALID_PASSWORD = "Invalid password";

    public static final String DECREASE_FAILED_PRODUCT = "Negative number of produts not allowed";
    public static final String NO_PRODUCTS = "Need to select at least one product";

    public static final String MAX_VOUCHERS = "Can't select more than two vouchers";
    public static final String TWO_DISCOUNTS = "Only one 5% discount voucher allowed";
    public static final String ORDER_IN_PROGRESS = "Sending order to validation terminal";

    /**
     * Shows toast message
     * @param toast - message to show
     */
    public static void showToast(final String toast, Activity activity) {
        activity.runOnUiThread(() -> Toast.makeText(activity, toast, Toast.LENGTH_LONG).show());
    }

}
