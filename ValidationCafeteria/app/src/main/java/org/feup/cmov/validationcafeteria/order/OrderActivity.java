package org.feup.cmov.validationcafeteria.order;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.feup.cmov.validationcafeteria.Constants;
import org.feup.cmov.validationcafeteria.MainActivity;
import org.feup.cmov.validationcafeteria.R;
import org.feup.cmov.validationcafeteria.dataStructures.Product;
import org.feup.cmov.validationcafeteria.dataStructures.Voucher;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    // products to validate
    ArrayList<Product> products = new ArrayList<>();

    // adapter to products' list
    ArrayAdapter<Product> productsAdapter;

    // accepted vouchers
    ArrayList<Voucher> vouchers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // testing stuff!
        /*Product p1 = new Product(2, "Coffee", 0.50, "coffee", 1);
        Product p2 = new Product(3, "Popcorn", 1.00, "popcorn", 2);

        products.add(p1);
        products.add(p2);

        Voucher v1 = new Voucher("dfiusdfdsuyf", Constants.FREE_COFFEE, 1.0);
        Voucher v2 = new Voucher("fsddsfsdf", Constants.DISCOUNT, 0.05);

        vouchers.add(v1);
        vouchers.add(v2);
        */

        initView(1, 20.00);     // TODO: to make this not hardcoded

    }

    private void initView(int order_no, double price_order) {
        TextView order_number = findViewById(R.id.order_number);
        String number = "Order No. " + order_no;
        order_number.setText(number);

        ListView list_products = findViewById(R.id.list_products);
        productsAdapter = new ProductAdapter(this, products);
        list_products.setAdapter(productsAdapter);

        Button vouchersBtn = findViewById(R.id.btn_see_vouchers);
        vouchersBtn.setOnClickListener((View v)->seeVouchers());

        if (vouchers.size() == 0) {
            //vouchersBtn.setEnabled(false);
        }

        TextView voucherSize = findViewById(R.id.text_accepted_vouchers);
        String acceptedVouchers = vouchers.size() + " accepted vouchers";
        voucherSize.setText(acceptedVouchers);

        TextView totalPrice = findViewById(R.id.total_price);
        String price = "Total price: " + price_order + " €";
        totalPrice.setText(price);

        Button terminateBtn = findViewById(R.id.btn_close);
        terminateBtn.setOnClickListener((View v)->terminate());
    }

    private void seeVouchers() {
        if (vouchers.size() > 0) {
            SeeVouchersFragment dialog = SeeVouchersFragment.constructor(vouchers);
            dialog.show(getSupportFragmentManager(), "seevouchers");
        } else {
            Constants.showToast(Constants.NO_VOUCHERS, this);
        }
    }

    private void terminate() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
