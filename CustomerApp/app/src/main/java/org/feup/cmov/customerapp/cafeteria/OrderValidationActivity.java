package org.feup.cmov.customerapp.cafeteria;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.WriterException;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Order;
import org.feup.cmov.customerapp.dataStructures.Product;
import org.feup.cmov.customerapp.dataStructures.User;
import org.feup.cmov.customerapp.dataStructures.Voucher;
import org.feup.cmov.customerapp.utils.Constants;
import org.feup.cmov.customerapp.utils.MyQRCode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderValidationActivity extends AppCompatActivity {

    // tickets to validate
    ArrayList<Product> products = new ArrayList<>();

    // vouchers to validate
    ArrayList<Voucher> vouchers = new ArrayList<>();

    // order
    String signedMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_validation);

        Bundle argument = getIntent().getExtras();

        if (argument != null) {
            signedMessage = argument.getString(Constants.ORDER_VALIDATION);
        }

        ImageView qrQode = findViewById(R.id.qrCodeCafeteria);
        new Thread(() -> {
            try {
                Bitmap bitmap = MyQRCode.create(signedMessage, 500);
                runOnUiThread(() -> qrQode.setImageBitmap(bitmap));
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
