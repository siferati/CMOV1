package org.feup.cmov.customerapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.feup.cmov.customerapp.dataStructures.CreditCard;
import org.feup.cmov.customerapp.database.Register;

public class RegisterActivity extends AppCompatActivity implements CreditCardDialog.MyDialogCloseListener {
    private EditText text_username;
    private EditText text_password;
    private EditText text_rep_password;
    private EditText text_name;
    private EditText text_nif;
    private CheckBox check_credit_card;

    private Button btn_credit_card;
    private Button btn_register;
    private CreditCard card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        text_username = findViewById(R.id.username_register);
        text_password = findViewById(R.id.password_register);
        text_rep_password = findViewById(R.id.repeat_password);
        text_name = findViewById(R.id.name_register);
        text_nif = findViewById(R.id.nif_register);
        check_credit_card = findViewById(R.id.check_credit_card);

        btn_credit_card = findViewById(R.id.btn_credit_card);
        btn_register = findViewById(R.id.btn_register);

        btn_register.setOnClickListener((View v)->register());
        btn_credit_card.setOnClickListener((View v)->creditCardDialog());
    }

    public void register() {
        btn_register.setEnabled(false);

        if (!validateRegisterCredentials()) {
            btn_register.setEnabled(true);
        } else {

            String username = text_username.getText().toString();
            String password = text_password.getText().toString();
            String name = text_name.getText().toString();
            String nif = text_nif.getText().toString();

            Register register = new Register(this, username, password, name, nif);
            Thread thr = new Thread(register);
            thr.start();
        }
    }

    public void handleResponse(int code, String response) {
        if (code == 200) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getBaseContext(), "Register success", Toast.LENGTH_LONG).show();
                }
            });

            // add credit card
        } else {
            btn_register.setEnabled(true);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getBaseContext(), response, Toast.LENGTH_LONG).show();
                }
            });
        }

    }


    public void creditCardDialog() {
        CreditCardFragment dialog = CreditCardFragment.constructor(card);       // create dialog instance
        dialog.show(getSupportFragmentManager(),"get_card");               // show dialog
    }

    public boolean validateRegisterCredentials() {
        boolean valid = true;

        if(TextUtils.isEmpty(text_username.getText())) {
            text_username.setError("Enter a valid username");
            valid = false;
        } else {
            text_username.setError(null);
        }

        if(TextUtils.isEmpty(text_password.getText()) || text_password.getText().toString().length() < 4 ) {
            text_password.setError("At least 4 characters");
            valid = false;
        } else if(!text_password.getText().toString().equals(text_password.getText().toString())) {
            text_password.setError("Passwords don't match");
            text_rep_password.setError("Passwords don't match");
            valid = false;
        } else {
            text_password.setError(null);
            text_rep_password.setError(null);
        }

        if(TextUtils.isEmpty(text_name.getText())) {
            text_name.setError("Enter a valid name");
            valid = false;
        } else {
            text_name.setError(null);
        }

        if(TextUtils.isEmpty(text_nif.getText())) {
            text_nif.setError("Enter a valid NIF");
            valid = false;
        } else {
            text_nif.setError(null);
        }

        if(!check_credit_card.isChecked()) {
            check_credit_card.setError("Invalid Credit Card");
            valid = false;
        } else {
            check_credit_card.setError(null);
        }

        return valid;
    }

    @Override
    public void handleDialogClose(CreditCard card) {
        if (card == null) {
            this.card = null;
        } else {
            this.card = card;
            this.check_credit_card.setChecked(true);
            // btn_credit_card.setEnabled(false);
        }
    }
}
