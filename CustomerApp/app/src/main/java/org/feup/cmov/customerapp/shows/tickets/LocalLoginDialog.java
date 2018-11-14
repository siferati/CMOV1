package org.feup.cmov.customerapp.shows.tickets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.User;
import org.feup.cmov.customerapp.database.Login;
import org.feup.cmov.customerapp.shows.ShowActivity;
import org.feup.cmov.customerapp.utils.Constants;

public class LocalLoginDialog extends Dialog {
    /**
     * Called when dialog is closed
     */
    public interface MyDialogCloseListener
    {
        /**
         * After confirming purchase, go back to shows activity
         */
        void handleLocalLogin();
    }

    // interface object
    private MyDialogCloseListener dialogListener;

    // parent context
    private Context context;

    // parent activity
    private ShowActivity activity;

    // login button
    private Button loginButton;

    // username
    private String text_username = "";

    // password input
    private EditText text_password;

    public LocalLoginDialog(Context context, ShowActivity activity) {
        super(context);

        this.context = context;
        this.activity = activity;

        setTitle(Constants.LOCAL_LOGIN);
        setOwnerActivity((Activity) context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_local_login);

        setUsername();

        loginButton = findViewById(R.id.btn_local_login);
        Button closeBtn = findViewById(R.id.btn_close_LL);

        // getOwnerActivity() returns the Activity that owns this Dialog
        dialogListener = (MyDialogCloseListener) getOwnerActivity();

        // sets click listener on save button
        loginButton.setOnClickListener((View v)->login());

        // sets click listener on close button
        closeBtn.setOnClickListener((View v)->dismiss());
    }

    private void setUsername() {
        User user = User.loadLoggedinUser(User.LOGGEDIN_USER_PATH, context);

        EditText username = findViewById(R.id.local_username);
        text_username = user.getUsername();
        username.setText(text_username);
    }

    private void login() {
        // disable login button
        loginButton.setEnabled(false);

        text_password = findViewById(R.id.local_password);
        String password = text_password.getText().toString();

        // verify if either password isn't empty
        if (TextUtils.isEmpty(text_password.getText())) {
            Toast.makeText(context, Constants.INVALID_PASSWORD, Toast.LENGTH_LONG).show();

            // enable login button if login failed
            loginButton.setEnabled(true);
        } else {
            // connect to server
            Login login = new Login(text_username, password, null, this);
            Thread thr = new Thread(login);
            thr.start();
        }
    }

    /**
     * Handles response from server
     * @param code - response code from server
     * @param response - response message given by server
     */
    public void handleResponse(int code, String response) {
        if (code == Constants.OK_RESPONSE) {
            dialogListener.handleLocalLogin();
            dismiss();
        } else {
            enableLoginBtn(true);
            // show error response
            showToast(response);
        }
    }

    /**
     * Shows toast message
     * @param toast - message to show
     */
    public void showToast(final String toast)
    {
        activity.runOnUiThread(() -> Toast.makeText(activity, toast, Toast.LENGTH_LONG).show());
    }

    /**
     * Enables or disables login button
     * @param enabled - tells whether login button should be enabled (true) or not (false)
     */
    public void enableLoginBtn(boolean enabled) {
        activity.runOnUiThread(() -> loginButton.setEnabled(enabled));
    }

}
