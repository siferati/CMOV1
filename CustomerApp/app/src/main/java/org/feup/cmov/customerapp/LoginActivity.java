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

    private EditText text_username;
    private EditText text_password;
    private Button btn_login;
    private TextView text_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.text_username = findViewById(R.id.input_username);
        this.text_password = findViewById(R.id.input_password);
        this.btn_login = findViewById(R.id.btn_login);
        this.text_signup = findViewById(R.id.link_signup);

        btn_login.setOnClickListener((View v)->login());
        text_signup.setOnClickListener((View v)->signup());
    }

    public void login() {
        btn_login.setEnabled(false);

        if (!validateLoginCredentials()) {
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
            btn_login.setEnabled(true);
        } else {

            String username = text_username.getText().toString();
            String password = text_password.getText().toString();


            // Add user to SERVER
        }

    }

    public boolean validateLoginCredentials() {
        boolean valid = true;

        if(TextUtils.isEmpty(text_username.getText())) {
            text_username.setError("enter a valid username");
            valid = false;
        } else {
            text_username.setError(null);
        }

        if(TextUtils.isEmpty(text_password.getText())) {
            text_password.setError("enter a valid password");
            valid = false;
        } else {
            text_password.setError(null);
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
