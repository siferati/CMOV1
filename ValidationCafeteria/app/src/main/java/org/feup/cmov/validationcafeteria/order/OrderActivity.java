package org.feup.cmov.validationcafeteria.order;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.feup.cmov.validationcafeteria.util.Constants;
import org.feup.cmov.validationcafeteria.MainActivity;
import org.feup.cmov.validationcafeteria.R;
import org.feup.cmov.validationcafeteria.dataStructures.Order;
import org.feup.cmov.validationcafeteria.dataStructures.Product;
import org.feup.cmov.validationcafeteria.dataStructures.User;
import org.feup.cmov.validationcafeteria.dataStructures.Voucher;
import org.feup.cmov.validationcafeteria.server.GetUser;
import org.feup.cmov.validationcafeteria.server.MakeOrder;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    // API to get products from server
    public GetUser userAPI;

    // products to validate
    ArrayList<Product> products = new ArrayList<>();

    // adapter to products' list
    ArrayAdapter<Product> productsAdapter;

    // accepted vouchers
    ArrayList<Voucher> vouchers = new ArrayList<>();

    // order
    Order order = new Order();

    MakeOrder makeOrderAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        Bundle argument = getIntent().getExtras();

        if (argument != null) {
            order = (Order) argument.getSerializable(Constants.SEND_ORDER);
        }

        makeOrderAPI = new MakeOrder(this, order.getUserId(), order.getSignature(), order.getJsonString());
        Thread thr = new Thread(makeOrderAPI);
        thr.start();

        Button terminateBtn = findViewById(R.id.btn_close);
        terminateBtn.setOnClickListener((View v)->terminate());
    }

    public void handleResponseOrder(int code, String response, Order orderResponse) {
        if (code == Constants.OK_RESPONSE) {
            ArrayList<Voucher> validVouchers = orderResponse.getVouchers();
            order.deleteInvalidVouchers(validVouchers);

            products = order.getProducts();
            vouchers = order.getVouchers();

            initView(orderResponse.getOrderId(), orderResponse.getPrice());

            userAPI = new GetUser(this, order.getUserId());
            Thread thr = new Thread(userAPI);
            thr.start();
        } else {
            Constants.showToast(response, this);
        }
    }

    private void initView(int order_no, double price_order) {

        runOnUiThread(() -> {
            String number = "Order No. " + order_no;
            setTitle(number);

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
        });
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
        Intent intent = new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Handles response from server
     * @param code - response code from server
     * @param response - response message given by server
     * @param user - user info that we got from the server
     */
    public void handleResponse(int code, String response, User user) {
        runOnUiThread(() -> {
            TextView order_number = findViewById(R.id.user);
            TextView nif_layout = findViewById(R.id.nif);

            if (code == Constants.OK_RESPONSE) {
                String username = user.getUsername() + "'s Order";
                order_number.setText(username);

                String nif = "Nif: " + user.getNif();
                nif_layout.setText(nif);

            } else {
                order_number.setEnabled(false);
                nif_layout.setEnabled(false);
                // show error response
                Constants.showToast(response, this);
            }

        });
    }
}
