package org.feup.cmov.customerapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    Customer user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button registerButton = findViewById(R.id.btn_register);
        registerButton.setOnClickListener((View v)->register());
    }

    public void register() {
        EditText usernameText = findViewById(R.id.username_register);
        EditText passwordText = findViewById(R.id.password_register);
        EditText repeatPasswordText = findViewById(R.id.repeat_password);

        EditText nameText = findViewById(R.id.name_register);
        EditText nifText = findViewById(R.id.nif_register);

        EditText typeText = findViewById(R.id.type_register);
        EditText numberText = findViewById(R.id.number_register);
        EditText validityDateText = findViewById(R.id.validity_register);

        Button registerButton = findViewById(R.id.btn_register);
        registerButton.setEnabled(false);

        if (!validateRegisterCredentials(usernameText, passwordText, repeatPasswordText, nameText, nifText, typeText, numberText, validityDateText)) {
            Toast.makeText(getBaseContext(), "Register failed", Toast.LENGTH_LONG).show();
            registerButton.setEnabled(true);
        }

        String username = usernameText.toString();
        String password = passwordText.toString();
        String name = nameText.toString();
        String nif = nifText.toString();
        String type = typeText.toString();
        String number = numberText.toString();
        String validityDate = validityDateText.toString();

        user.registerUser(username, password, name, nif, type, number, validityDate);
    }

    public boolean validateRegisterCredentials(EditText username, EditText pass, EditText rep_pass,
                                               EditText name, EditText nif,
                                               EditText type, EditText number, EditText validity) {
        boolean valid = true;

        if(TextUtils.isEmpty(username.getText())) {
            username.setError("enter a valid username");
            valid = false;
        } else {
            username.setError(null);
        }

        if(TextUtils.isEmpty(pass.getText()) || pass.toString().length() < 4 ) {
            pass.setError("at least 4 characters");
            valid = false;
        } else if(!pass.toString().equals(rep_pass.toString())) {
            pass.setError("passwords don't match");
            rep_pass.setError("passwords don't match");
            valid = false;
        } else {
            pass.setError(null);
            rep_pass.setError(null);
        }

        if(TextUtils.isEmpty(name.getText())) {
            name.setError("enter a valid name");
            valid = false;
        } else {
            name.setError(null);
        }

        if(TextUtils.isEmpty(nif.getText())) {
            nif.setError("enter a valid NIF");
            valid = false;
        } else {
            nif.setError(null);
        }

        if(TextUtils.isEmpty(type.getText())) {
            type.setError("enter a valid type");
            valid = false;
        } else {
            type.setError(null);
        }

        if(TextUtils.isEmpty(number.getText())) {
            number.setError("enter a valid number");
            valid = false;
        } else {
            number.setError(null);
        }

        if(TextUtils.isEmpty(validity.getText())) {
            validity.setError("enter a valid validity date");
            valid = false;
        } else {
            validity.setError(null);
        }

        return valid;
    }
}
