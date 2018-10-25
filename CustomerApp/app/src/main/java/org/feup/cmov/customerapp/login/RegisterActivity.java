package org.feup.cmov.customerapp.login;

import android.content.Intent;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

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

        this.findViews();

        this.btn_register.setOnClickListener((View v)->register());
        this.btn_credit_card.setOnClickListener((View v)->creditCardDialog());
    }

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

    public void register() {
        btn_register.setEnabled(false);

        if (!validateRegisterCredentials()) {
            Toast.makeText(RegisterActivity.this, "Register failed", Toast.LENGTH_LONG).show();
            btn_register.setEnabled(true);
        } else {

            String username = text_username.getText().toString();
            String password = text_password.getText().toString();
            String name = text_name.getText().toString();
            String nif = text_nif.getText().toString();

            // generate rsa key pair
            try {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);

                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");

                keyPairGenerator.initialize(
                        new KeyPairGeneratorSpec.Builder(this)
                                .setKeySize(512)
                                .setAlias("key")
                                .setSubject(new X500Principal("CN=feup"))
                                .setSerialNumber(BigInteger.ONE)
                                .setStartDate(start.getTime())
                                .setEndDate(end.getTime())
                                .build());

                KeyPair keyPair = keyPairGenerator.generateKeyPair();

                Log.d("private key", "" + ((RSAPrivateKey)keyPair.getPrivate()).getPrivateExponent());
            }
            catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }


            User.setUser(name, username, password, nif, card);

            Register register = new Register(this, username, password, name, nif);
            Thread thr = new Thread(register);
            thr.start();
        }
    }

    public void handleResponse(int code, String response) {
        if (code == 200) {
            JSONObject resp = null;
            String result_id = null;

            try {
                resp = new JSONObject(response);
                result_id = resp.get("id").toString();
            } catch(JSONException e) {
                e.getStackTrace();
            }

            User.setId(result_id);

            AddCreditCard addCreditCard = new AddCreditCard(this, User.getId(), this.card);
            Thread thr = new Thread(addCreditCard);
            thr.start();
        } else {
            showToast(response);
            enableRegisterBtn(true);
        }
    }

    public void handleResponseCC(int code, String response) {
        if (code == 200) {
            showToast("Register success");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            showToast(response);
            btn_register.setEnabled(true);
        }
    }

    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, toast, Toast.LENGTH_LONG).show());
    }

    public void enableRegisterBtn(boolean enabled) {
        runOnUiThread(() -> btn_register.setEnabled(enabled));
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
