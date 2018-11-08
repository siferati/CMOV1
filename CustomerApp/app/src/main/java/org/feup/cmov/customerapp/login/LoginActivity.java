package org.feup.cmov.customerapp.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.feup.cmov.customerapp.MainActivity;
import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.User;
import org.feup.cmov.customerapp.database.Login;
import org.feup.cmov.customerapp.utils.Constants;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    //private static final int REQUEST_SIGNUP = 0;

    // username input
    private EditText text_username;

    // password input
    private EditText text_password;

    // login button
    private Button btn_login;

    // sign up text
    private TextView text_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // find layout views by id
        this.text_username = findViewById(R.id.input_username);
        this.text_password = findViewById(R.id.input_password);
        this.btn_login = findViewById(R.id.btn_login);
        this.text_signup = findViewById(R.id.link_signup);

        // set listener on login button
        btn_login.setOnClickListener((View v)->login());

        // set listener on sign up message to start register activity
        text_signup.setOnClickListener((View v)->signup());
    }

    /**
     * Function called when user tries to login
     */
    public void login() {
        // disable login button
        btn_login.setEnabled(false);

        // verify if either username or password is valid (ie, not empty)
        if (!validateLoginCredentials()) {
            Toast.makeText(getBaseContext(), Constants.LOGIN_FAILED, Toast.LENGTH_LONG).show();

            // enable login button if login failed
            btn_login.setEnabled(true);
        } else {
            String username = text_username.getText().toString();
            String password = text_password.getText().toString();

            // connect to server
            Login login = new Login(this, username, password);
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
            // show login success message
            showToast(Constants.LOGIN_SUCCESS);

            String username = text_username.getText().toString();
            User.setLoggedinUser(username, User.LOGGEDIN_USER_PATH, getApplicationContext());

            // start main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            // show error response
            showToast(response);

            // enable login button if login failed
            enableLoginBtn(true);
        }
    }

    /**
     * Shows toast message
     * @param toast - message to show
     */
    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(LoginActivity.this, toast, Toast.LENGTH_LONG).show());
    }

    /**
     * Enables or disables login button
     * @param enabled - tells whether login button should be enabled (true) or not (false)
     */
    public void enableLoginBtn(boolean enabled) {
        runOnUiThread(() -> btn_login.setEnabled(enabled));
    }

    /**
     * Validates username and password (checks if not null)
     * @return true if valid credentials, false if not
     */
    public boolean validateLoginCredentials() {
        boolean valid = true;

        if(TextUtils.isEmpty(text_username.getText())) {
            text_username.setError("Enter a valid username");
            valid = false;
        } else {
            text_username.setError(null);
        }

        if(TextUtils.isEmpty(text_password.getText())) {
            text_password.setError("Enter a valid password");
            valid = false;
        } else {
            text_password.setError(null);
        }

        return valid;
    }

    /**
     * Calls register activity
     */
    public void signup() {
        // Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        // startActivityForResult(intent, REQUEST_SIGNUP);
        // finish();

        // Start the Signup activity
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
