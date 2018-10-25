package org.feup.cmov.customerapp.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.feup.cmov.customerapp.MainActivity;
import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.User;
import org.feup.cmov.customerapp.database.Login;

public class LoginActivity extends AppCompatActivity {
    //private static final int REQUEST_SIGNUP = 0;

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

            Login login = new Login(this, username, password);
            Thread thr = new Thread(login);
            thr.start();
        }

    }

    public void handleResponse(int code, String response) {
        if (code == 200) {
            User.setLoggedIn(true);

            showToast("Login Success");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            showToast(response);
            enableLoginBtn(true);
        }
    }

    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(LoginActivity.this, toast, Toast.LENGTH_LONG).show());
    }

    public void enableLoginBtn(boolean enabled) {
        runOnUiThread(() -> btn_login.setEnabled(enabled));
    }

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

    public void signup() {
        // Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        // startActivityForResult(intent, REQUEST_SIGNUP);
        // finish();

        // Start the Signup activity
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
