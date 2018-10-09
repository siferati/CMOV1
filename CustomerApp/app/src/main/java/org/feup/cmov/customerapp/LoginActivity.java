package org.feup.cmov.customerapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_SIGNUP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.btn_login);
        TextView signupLink = findViewById(R.id.link_signup);

        loginButton.setOnClickListener((View v)->login());
        signupLink.setOnClickListener((View v)->signup());
    }

    public void login() {
        EditText usernameText = findViewById(R.id.input_username);
        EditText passwordText = findViewById(R.id.input_password);

        Button loginButton = findViewById(R.id.btn_login);
        loginButton.setEnabled(false);

        if (!validateLoginCredentials(usernameText, passwordText)) {
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
            loginButton.setEnabled(true);
        }

        String username = usernameText.toString();
        String password = passwordText.toString();


        // Add user to SERVER

    }

    public boolean validateLoginCredentials(EditText username, EditText password) {
        boolean valid = true;

        if(TextUtils.isEmpty(username.getText())) {
            username.setError("enter a valid username");
            valid = false;
        } else {
            username.setError(null);
        }

        if(TextUtils.isEmpty(password.getText())) {
            password.setError("enter a valid password");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    public void signup() {
        // Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        // startActivityForResult(intent, REQUEST_SIGNUP);
        // finish();

        // Start the Signup activity
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
