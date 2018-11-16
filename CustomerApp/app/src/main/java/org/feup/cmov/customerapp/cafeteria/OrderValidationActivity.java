package org.feup.cmov.customerapp.cafeteria;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Product;
import org.feup.cmov.customerapp.dataStructures.Voucher;
import org.feup.cmov.customerapp.utils.Constants;

import java.util.ArrayList;

public class OrderValidationActivity extends AppCompatActivity {

    // tickets to validate
    ArrayList<Product> products;

    // vouchers to validate
    ArrayList<Voucher> vouchers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_validation);

        Bundle argument = getIntent().getExtras();

        products = new ArrayList<>();
        vouchers = new ArrayList<>();
        if (argument != null) {
            products = (ArrayList<Product>) argument.getSerializable(Constants.ORDER_VALIDATION);
            vouchers = (ArrayList<Voucher>) argument.getSerializable(Constants.VOUCHERS_VALIDATION);
        }

        ImageView qr_code = findViewById(R.id.qrCodeCafeteria);
    }
}
