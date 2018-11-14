package org.feup.cmov.customerapp.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.CreditCard;
import org.feup.cmov.customerapp.dataStructures.User;
import org.feup.cmov.customerapp.database.AddCreditCard;
import org.feup.cmov.customerapp.database.Register;
import org.feup.cmov.customerapp.utils.Constants;
import org.feup.cmov.customerapp.utils.MyCrypto;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;

public class RegisterActivity extends AppCompatActivity implements CreditCardDialog.MyDialogCloseListener {

    // username input
    private EditText text_username;

    // password input
    private EditText text_password;

    // repeat password input
    private EditText text_rep_password;

    // name input
    private EditText text_name;

    // nif input
    private EditText text_nif;

    // checkbox determining whether credit card has been inserted or not
    private CheckBox check_credit_card;

    // credit card button
    private Button btn_credit_card;

    // register button
    private Button btn_register;

    // credit card
    private CreditCard card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.findViews();

        this.btn_register.setOnClickListener((View v)->register());
        this.btn_credit_card.setOnClickListener((View v)->creditCardDialog());
    }

    /**
     * Finds register layout views
     */
    public void findViews() {
        this.text_username = findViewById(R.id.username_register);
        this.text_password = findViewById(R.id.password_register);
        this.text_rep_password = findViewById(R.id.repeat_password);
        this.text_name = findViewById(R.id.name_register);
        this.text_nif = findViewById(R.id.nif_register);
        this.check_credit_card = findViewById(R.id.check_credit_card);

        this.btn_credit_card = findViewById(R.id.btn_credit_card);
        this.btn_register = findViewById(R.id.btn_register);
    }

    /**
     * Function called when user tries to register
     */
    public void register() {
        btn_register.setEnabled(false);

        // verify if register credentials are valid
        if (!validateRegisterCredentials()) {
            Toast.makeText(RegisterActivity.this, Constants.REGISTER_FAILED, Toast.LENGTH_LONG).show();

            // enable register button if register failed
            btn_register.setEnabled(true);
        } else {
            String username = text_username.getText().toString();
            String password = text_password.getText().toString();
            String name = text_name.getText().toString();
            String nif = text_nif.getText().toString();

            // generate RSA key pair
            String keyN = "";
            String keyE = "";
            try {

                RSAPublicKey publicKey = (RSAPublicKey) MyCrypto.generateRSAKeypair(this, username);
                keyN = publicKey.getModulus().toString(16);
                keyE = publicKey.getPublicExponent().toString(16);

            } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | IOException | KeyStoreException | CertificateException | UnrecoverableEntryException e) {
                // TODO
                e.printStackTrace();
            }

            Register register = new Register(this, username, password, name, nif, keyN, keyE);
            Thread thr = new Thread(register);
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
            JSONObject resp;
            String result_id = null;

            try {
                resp = new JSONObject(response);

                // if register is successful, server returns an id
                result_id = resp.get("id").toString();
            } catch(JSONException e) {
                e.getStackTrace();
            }

            String username = text_username.getText().toString();
            String password = text_password.getText().toString();
            String name = text_name.getText().toString();
            String nif = text_nif.getText().toString();

            // set user's parameters
            User user = new User(result_id, username, password, name, nif, card);

            // locally save user
            User.saveUser(user, User.USER_PATH, getApplicationContext());

            // add credit card to user
            AddCreditCard addCreditCard = new AddCreditCard(this, result_id, this.card);
            Thread thr = new Thread(addCreditCard);
            thr.start();
        } else {
            showToast(response);

            // enable register button if register failed
            enableRegisterBtn(true);
        }
    }

    public void handleResponseCC(int code, String response) {
        if (code == Constants.OK_RESPONSE) {
            showToast(Constants.REGISTER_SUCCESS);

            // start login activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            showToast(response);

            // enable register button if register failed
            enableRegisterBtn(true);
        }
    }

    /**
     * Shows toast message
     * @param toast - message to show
     */
    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, toast, Toast.LENGTH_LONG).show());
    }

    /**
     * Enables or disables register button
     * @param enabled - tells whether register button should be enabled (true) or not (false)
     */
    public void enableRegisterBtn(boolean enabled) {
        runOnUiThread(() -> btn_register.setEnabled(enabled));
    }

    /**
     * Show credit card dialog
     */
    public void creditCardDialog() {
        CreditCardFragment dialog = CreditCardFragment.constructor(card);       // create dialog instance
        dialog.show(getSupportFragmentManager(),"get_card");               // show dialog
    }

    /**
     * Validates register credentials
     * @return true if valid credentials, false if not
     */
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
        } else if(!text_password.getText().toString().equals(text_rep_password.getText().toString())) {
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

    /**
     * Handles closing of credit card dialog
     * @param card - credit card to be set on dialog fragment
     */
    @Override
    public void handleDialogClose(CreditCard card) {
        if (card == null) {
            this.card = null;
        } else {
            this.card = card;
            this.check_credit_card.setChecked(true);
            //btn_credit_card.setEnabled(false);
        }
    }
}
