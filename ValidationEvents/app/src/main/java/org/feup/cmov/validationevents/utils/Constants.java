package org.feup.cmov.validationevents.utils;

import android.app.Activity;
import android.widget.Toast;

public class Constants {

    public static final int OK_RESPONSE = 200;
    public static final int NO_INTERNET = -1;
    public static final int SERVER_TIMEOUT = 2000;
    public static final String ERROR_CONNECTING = "Error connecting";

    public static final int SHOWS_PER_LOAD = 10;
    public static final String NO_SELECTED_SHOW = "No Selected Show";
    public static final String INVALID_SHOW = "Tickets presented aren't for this show";

    public static final String USER_ID = "user_id";
    public static final String SHOW_ID = "show_id";
    public static final String VALIDATE_TICKETS = "validate_tickets";

    /**
     * Shows toast message
     * @param toast - message to show
     */
    public static void showToast(final String toast, Activity activity) {
        activity.runOnUiThread(() -> Toast.makeText(activity, toast, Toast.LENGTH_LONG).show());
    }
}
